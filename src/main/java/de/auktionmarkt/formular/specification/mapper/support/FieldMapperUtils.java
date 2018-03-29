package de.auktionmarkt.formular.specification.mapper.support;

import de.auktionmarkt.formular.specification.FieldSpecification;
import de.auktionmarkt.formular.specification.annotation.FormInput;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.convert.TypeDescriptor;

import java.beans.PropertyDescriptor;
import java.util.Objects;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldMapperUtils {

    /**
     * Returns the element type of a collection or the type of the given {@code typeDescriptor} when it is not a
     * {@link java.util.Collection}. For {@link java.util.Collection}s firstly the {@link FormInput#elementType()} is
     * considered. If this is set to {@code void.class} (default value) the element type will be retrieved from their
     * generic parameters. If those are not set or unavailable {@code null} is returned.
     *
     * @param typeDescriptor A {@link TypeDescriptor}
     * @return The unpacked type or {@code null} when a collection is given which is not parametrized and no
     *         {@link FormInput#elementType()} is specified
     */
    public static Class<?> unpackType(TypeDescriptor typeDescriptor) {
        Objects.requireNonNull(typeDescriptor, "typeDescriptor must not be null");
        if (typeDescriptor.isCollection()) {
            FormInput formInput = typeDescriptor.getAnnotation(FormInput.class);
            Class<?> elementType = formInput.elementType();
            if (elementType == void.class) {
                typeDescriptor = typeDescriptor.getElementTypeDescriptor();
                return typeDescriptor != null ? typeDescriptor.getType() : null;
            } else {
                // Primitives are no valid collection element types. Only their wrappers are.
                // Maybe throw an exception instead of return null?
                if (elementType.isPrimitive())
                    return null;
                return elementType;
            }
        } else {
            return typeDescriptor.getType();
        }
    }

    public static FieldSpecification.Builder setTypeIfAbsent(FieldSpecification.Builder builder, String type) {
        String previousType = builder.type();
        return previousType == null || previousType.isEmpty() ? builder.type(type) : builder;
    }

    @SuppressWarnings("unchecked")
    public static Supplier<String> getStringSupplier(BeanFactory beanFactory, PropertyDescriptor propertyDescriptor,
                                                     String beanName, String labelValue) {
        if (labelValue.isEmpty()) {
            if (beanName.isEmpty())
                return propertyDescriptor::getDisplayName;
            else
                return beanFactory.getBean(beanName, Supplier.class);
        } else {
            return () -> labelValue;
        }
    }
}
