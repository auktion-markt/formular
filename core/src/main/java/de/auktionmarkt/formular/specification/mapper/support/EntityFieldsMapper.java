/*
 *    Copyright 2018 Auktion & Markt AG
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.auktionmarkt.formular.specification.mapper.support;

import de.auktionmarkt.formular.specification.FieldSpecification;
import de.auktionmarkt.formular.specification.FieldTypes;
import de.auktionmarkt.formular.specification.annotation.EntityReference;
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
import org.springframework.data.repository.support.Repositories;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

/**
 * An implementation of {@link FieldsMapper} for mapping fields which represents a OneToOne, OneToMany, ManyToOne or
 * ManyToMany relation.
 * <p>
 * Implementation details. Attribute information is obtained from {@link EntityManagerFactory#getMetamodel()}.
 * spring-data repository information and primary keys is get from {@link Repositories}. Entity-to-string and
 * primary-key-to-string conversion is done using the global conversion service.
 */
public class EntityFieldsMapper extends AbstractAnnotatedInputFieldsMapper {

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
        System.out.println("Property asd " + propertyDescriptor.getName() + ": " + typeDescriptor.hasAnnotation(EntityReference.class) + " "  + super.supportsField(model, propertyDescriptor, typeDescriptor));
        return typeDescriptor.hasAnnotation(EntityReference.class) &&
                super.supportsField(model, propertyDescriptor, typeDescriptor);
    }

    @Override
    public Collection<FieldSpecification> mapFieldSpecification(FormMapper callingFormMapper, Class<?> model,
                                                                PropertyDescriptor propertyDescriptor,
                                                                TypeDescriptor typeDescriptor) {
        EntityReference entityReference = typeDescriptor.getAnnotation(EntityReference.class);
        Class<?> entityClass = entityReference.entityClass();
        if (entityClass == void.class) {
            if (typeDescriptor.isCollection()) {
                TypeDescriptor elementTypeDescriptor = typeDescriptor.getElementTypeDescriptor();
                if (elementTypeDescriptor == null) {
                    throw new FormMappingException("Cannot determine entity reference type. Specify " +
                            "@EntityReference(entityClass = YourEntity.class) or parameterize collection");
                }
                entityClass = elementTypeDescriptor.getType();
            } else {
                entityClass = typeDescriptor.getType();
            }
        }

        Supplier<? extends Iterable<?>> rawValueSupplier;
        if (typeDescriptor.hasAnnotation(JpaValuesByRepositoryMethod.class)) {
            JpaValuesByRepositoryMethod supplierConfig =
                    typeDescriptor.getAnnotation(JpaValuesByRepositoryMethod.class);
            rawValueSupplier = getRepositorySupplier(entityClass, model, propertyDescriptor, typeDescriptor, supplierConfig);
        } else {
            rawValueSupplier = getFindAllSupplier(entityClass);
        }
        String type = typeDescriptor.getAnnotation(FormInput.class).type();
        if (type.isEmpty())
            type = typeDescriptor.isCollection() ? FieldTypes.CHECKBOX : FieldTypes.SELECT;
        Supplier<Map<String, String>> valueSupplier = valueSupplier(entityClass, rawValueSupplier);
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

    private <T> Supplier<? extends Iterable<?>> getFindAllSupplier(Class<T> entityType) {
        return () -> {
            EntityManager em = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityType);
            Root<T> root = cq.from(entityType);
            cq = cq.select(root);
            TypedQuery<?> query = em.createQuery(cq);
            return query.getResultList();
            // Entity manager should remain in a open-state to allows fetching references. Maybe close entity manager
            // when entity graphs are supported
        };
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
            PersistenceUnitUtil persistenceUnitUtil = entityManagerFactory.getPersistenceUnitUtil();
            Map<String, String> result = new HashMap<>();
            do {
                Object instance = iterator.next();
                Object id = persistenceUnitUtil.getIdentifier(instance);
                String key = conversionService.convert(id, String.class);
                String displayValue = conversionService.convert(instance, String.class);
                result.put(key, displayValue);
            } while(iterator.hasNext());
            return result;
        };
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
