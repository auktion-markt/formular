package de.auktionmarkt.formular.specification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.core.convert.TypeDescriptor;

import javax.xml.bind.annotation.XmlTransient;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Data
public class FieldSpecification {

    @JsonIgnore
    @XmlTransient
    private final PropertyDescriptor propertyDescriptor;
    @JsonIgnore
    @XmlTransient
    private final TypeDescriptor typeDescriptor;
    private final Supplier<String> labelSupplier;
    private final String path;
    private final String type;
    private final Map<String, Object> parameters;
    private final int order;
    private final Supplier<Map<String, String>> valuesSupplier;

    public String getLabel() {
        return getLabelSupplier().get();
    }

    public static Builder builder() {
        return new Builder();
    }

    // @Builder annotation of lombok would be great but doesn't allow to access already set properties
    public static class Builder {

        private PropertyDescriptor propertyDescriptor;
        private TypeDescriptor typeDescriptor;
        private Supplier<String> labelSupplier;
        private String path;
        private String type;
        private Map<String, Object> parameters;
        private int order;
        private Supplier<Map<String, String>> valuesSupplier;

        private Builder() {
        }

        public Builder propertyDescriptor(PropertyDescriptor propertyDescriptor) {
            this.propertyDescriptor = propertyDescriptor;
            return this;
        }

        public PropertyDescriptor propertyDescriptor() {
            return propertyDescriptor;
        }

        public Builder typeDescriptor(TypeDescriptor typeDescriptor) {
            this.typeDescriptor = typeDescriptor;
            return this;
        }

        public TypeDescriptor typeDescriptor() {
            return typeDescriptor;
        }

        public Builder labelSupplier(Supplier<String> labelSupplier) {
            this.labelSupplier = labelSupplier;
            return this;
        }

        public Supplier<String> labelSupplier() {
            return labelSupplier;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public String path() {
            return path;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public String type() {
            return type;
        }

        public Builder parameters(Map<String, Object> parameters) {
            this.parameters = parameters;
            return this;
        }

        public Map<String, Object> parameters() {
            return parameters;
        }

        public Builder parameter(String key, Object value) {
            Map<String, Object> parameters = parameters();
            if (parameters == null)
                parameters = new HashMap<>();
            parameters.put(key, value);
            return parameters(parameters);
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public int order() {
            return order;
        }

        public Builder valueSupplier(Supplier<Map<String, String>> valuesSupplier) {
            this.valuesSupplier = valuesSupplier;
            return this;
        }

        public Supplier<Map<String, String>> valuesSupplier() {
            return valuesSupplier;
        }

        public FieldSpecification build() {
            return new FieldSpecification(propertyDescriptor(), typeDescriptor(), labelSupplier() ,path(), type(),
                    parameters(), order(), valuesSupplier());
        }
    }
}
