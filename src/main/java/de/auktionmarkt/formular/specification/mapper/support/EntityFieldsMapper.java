package de.auktionmarkt.formular.specification.mapper.support;

import de.auktionmarkt.formular.internal.CollectionUtils;
import de.auktionmarkt.formular.specification.FieldSpecification;
import de.auktionmarkt.formular.specification.FieldTypes;
import de.auktionmarkt.formular.specification.annotation.FormInput;
import de.auktionmarkt.formular.specification.annotation.JpaValuesByRepositoryMethod;
import de.auktionmarkt.formular.specification.mapper.AbstractAnnotatedInputFieldsMapper;
import de.auktionmarkt.formular.specification.mapper.FieldsMapper;
import de.auktionmarkt.formular.specification.mapper.FormMapper;
import de.auktionmarkt.formular.specification.mapper.FormMappingException;
import de.auktionmarkt.formular.specification.support.RepositoryParameterFiller;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.support.Repositories;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * An implementation of {@link FieldsMapper} for mapping fields which represents a OneToOne, OneToMany, ManyToOne or
 * ManyToMany relation.
 * <p>
 * Implementation details. Attribute information is obtained from {@link EntityManagerFactory#getMetamodel()}.
 * spring-data repository information and primary keys is get from {@link Repositories}. Entity-to-string and
 * primary-key-to-string conversion is done using the global conversion service.
 */
// ToDo: Maybe support for MapAttributes
public class EntityFieldsMapper extends AbstractAnnotatedInputFieldsMapper {

    private static final Set<Attribute.PersistentAttributeType> SUPPORTED_PERSISTENCE_ATTRIBUTE_TYPES =
            CollectionUtils.warpImmutableSet(Attribute.PersistentAttributeType.MANY_TO_MANY,
                    Attribute.PersistentAttributeType.MANY_TO_ONE, Attribute.PersistentAttributeType.ONE_TO_MANY,
                    Attribute.PersistentAttributeType.ONE_TO_ONE);

    private final EntityManagerFactory entityManagerFactory;
    private final BeanFactory beanFactory;
    private final Repositories repositories;

    public EntityFieldsMapper(ConversionService conversionService, EntityManagerFactory entityManagerFactory,
                              ListableBeanFactory listableBeanFactory) {
        super(listableBeanFactory, conversionService);
        this.entityManagerFactory = entityManagerFactory;
        beanFactory = listableBeanFactory;
        repositories = new Repositories(listableBeanFactory);
    }

    @Override
    public boolean supportsField(Class<?> model, PropertyDescriptor propertyDescriptor, TypeDescriptor typeDescriptor) {
        // Check if this field qualifies as a form field
        if (!super.supportsField(model, propertyDescriptor, typeDescriptor))
            return false;
        Attribute<?, ?> attribute = getAttribute(model, propertyDescriptor);
        // Only mapped entities are supported
        if (attribute == null)
            return false;
        Attribute.PersistentAttributeType persistentAttributeType = attribute.getPersistentAttributeType();
        // Only associations are supported; a supported selector annotation (@JpaValuesByRepositoryMethod) is required
        if (!SUPPORTED_PERSISTENCE_ATTRIBUTE_TYPES.contains(persistentAttributeType) ||
                (!typeDescriptor.hasAnnotation(JpaValuesByRepositoryMethod.class))) {
            return false;
        }
        Type.PersistenceType persistenceType = null;
        if (attribute instanceof CollectionAttribute)
            persistenceType = ((CollectionAttribute) attribute).getElementType().getPersistenceType();
        else if (attribute instanceof SingularAttribute)
            persistenceType = ((SingularAttribute) attribute).getType().getPersistenceType();
        return persistenceType == Type.PersistenceType.ENTITY;
    }

    @Override
    public Collection<FieldSpecification> mapFieldSpecification(FormMapper callingFormMapper, Class<?> model,
                                                                PropertyDescriptor propertyDescriptor,
                                                                TypeDescriptor typeDescriptor) {
        Metamodel metamodel = entityManagerFactory.getMetamodel();
        ManagedType<?> managedType = metamodel.managedType(model);
        Attribute<?, ?> attribute = managedType.getAttribute(propertyDescriptor.getName());
        Objects.requireNonNull(attribute, "Attribute not available");

        Supplier<? extends Iterable<?>> rawValueSupplier;
        JpaValuesByRepositoryMethod jpaValuesByRepositoryMethod = typeDescriptor
                .getAnnotation(JpaValuesByRepositoryMethod.class);
        Class<?> entityType = getEntityType(attribute);
        if (jpaValuesByRepositoryMethod != null) {
            rawValueSupplier = getRepositorySupplier(entityType, model, propertyDescriptor,
                    typeDescriptor, jpaValuesByRepositoryMethod);
        } else {
            throw new UnsupportedOperationException("Cannot determine value supplier");
        }
        String type = typeDescriptor.getAnnotation(FormInput.class).type();
        if (type.isEmpty())
            type = typeDescriptor.isCollection() ? FieldTypes.CHECKBOX : FieldTypes.SELECT;
        Supplier<Map<String, String>> valueSupplier = valueSupplier(entityType, rawValueSupplier);
        return Collections.singleton(prepareMapFieldSpecification(propertyDescriptor, typeDescriptor)
                .valuesSupplier(valueSupplier)
                .type(type)
                .build());
    }

    /**
     * Creates a {@link Supplier} which calls the selected repository method via cglib.
     */
    private Supplier<? extends Iterable<?>> getRepositorySupplier(Class<?> entityType, Class<?> model,
                                                                  PropertyDescriptor propertyDescriptor,
                                                                  TypeDescriptor typeDescriptor,
                                                                  JpaValuesByRepositoryMethod selector) {
        Class<?>[] parameters = selector.parameters();
        RepositoryParameterFiller filler;
        if (parameters.length > 0) {
            if (selector.fillerBeanName().isEmpty()) {
                throw new FormMappingException("Missing filler class on " + entityType.getName() + " on field " +
                        propertyDescriptor.getName());
            }
            try {
                filler = beanFactory.getBean(RepositoryParameterFiller.class, selector.fillerBeanName());
            } catch (Throwable throwable) {
                throw new FormMappingException("Cannot get filler bean", throwable);
            }
        } else {
            // By assigning value on an else branch, filler gets effective final as it is required by lambdas
            filler = null;
        }
        Object repository = repositories.getRepositoryFor(entityType);
        if (repository == null)
            throw new FormMappingException("No repository available for " + entityType);
        FastMethod fastMethod = wrapRepositoryMethod(repository, selector.value(), parameters);
        return () -> {
            Object[] resolvedParameters = filler != null ?
                    filler.getParameters(entityType, model, propertyDescriptor, typeDescriptor) :
                    new Object[0];
            try {
                return (Iterable) fastMethod.invoke(repository, resolvedParameters);
            } catch (InvocationTargetException e) {
                throw new UnsupportedOperationException("Calling repository method throws " +
                        "an InvocationTargetException", e);
            }
        };
    }

    private Attribute<?, ?> getAttribute(Class<?> model, PropertyDescriptor propertyDescriptor) {
        Metamodel metamodel = entityManagerFactory.getMetamodel();
        ManagedType<?> managedType = metamodel.managedType(model);
        if (managedType == null)
            return null;
        Attribute<?, ?> attribute = managedType.getAttribute(propertyDescriptor.getName());
        if (attribute == null)
            return null;
        return attribute;
    }

    /**
     * Converts a {@link Supplier} which supplies an {@link Iterable} of entities to a {@link Supplier} which offers
     * a {@link Map}&lte;{@link String}, {@link String}&gte;.
     */
    @SuppressWarnings("unchecked")
    private Supplier<Map<String, String>> valueSupplier(Class<?> entityType,
                                                        Supplier<? extends Iterable<?>> iterable) {
        return () -> {
            Iterator<?> iterator = iterable.get().iterator();
            if (!iterator.hasNext())
                return Collections.emptyMap();
            EntityInformation entityInformation = repositories.getEntityInformationFor(entityType);
            Map<String, String> result = new HashMap<>();
            do {
                Object instance = iterator.next();
                Serializable id = entityInformation.getId(instance);
                String key = conversionService.convert(id, String.class);
                String displayValue = conversionService.convert(instance, String.class);
                result.put(key, displayValue);
            } while(iterator.hasNext());
            return result;
        };
    }

    /**
     * Extracts the entity type ot of an attribute.
     */
    private static Class<?> getEntityType(Attribute<?, ?> attribute) {
        if (!SUPPORTED_PERSISTENCE_ATTRIBUTE_TYPES.contains(attribute.getPersistentAttributeType())) {
            throw new UnsupportedOperationException("Unsupported PersistentAttributeType: " +
                    attribute.getPersistentAttributeType());
        }
        return attribute instanceof CollectionAttribute ?
                ((CollectionAttribute) attribute).getElementType().getJavaType() : attribute.getJavaType();
    }

    /**
     * Wraps the Method matching name {@code methodName} and {@code parameterTypes} into a {@link FastMethod} for
     * prevent using reflection.
     */
    private static FastMethod wrapRepositoryMethod(Object instance, String methodName, Class<?>[] parameterTypes) {
        FastClass repositoryFastClass = FastClass.create(instance.getClass());
        FastMethod fastMethod;
        try {
            fastMethod = repositoryFastClass.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodError e) {
            throw new FormMappingException("No such method: " + methodName + " (parameters: " +
                    Arrays.toString(parameterTypes) + ") on repository class " + instance.getClass());
        }
        if (!Iterable.class.isAssignableFrom(fastMethod.getReturnType())) {
            throw new FormMappingException("Unsupported return type on method " + fastMethod.getName() +
                    " (parameters: " + Arrays.toString(parameterTypes) + ") on class " +
                    repositoryFastClass.getName() + ": " + fastMethod.getReturnType());
        }
        return fastMethod;
    }
}
