package de.auktionmarkt.formular.specification;

import lombok.Data;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContext;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class FormSpecification {

    private final Class<?> specifiedClass;
    private final String method;
    private final String actionScheme;
    private final Map<String, FieldSpecification> fields;

    public String getAction() {
        return getAction(Collections.emptyMap());
    }

    public String getAction(Map<String, String> parameters) {
        Objects.requireNonNull(parameters, "parameters must not be null");
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes))
            return null;
        ServletRequestAttributes servletAttributes = (ServletRequestAttributes) requestAttributes;
        return new RequestContext(servletAttributes.getRequest(), servletAttributes.getResponse())
                .getContextUrl(getActionScheme(), parameters);
    }

    public static FormSpecification create(Class<?> specifiedClass, String method, String actionScheme,
                                           Collection<FieldSpecification> fieldSpecifications) {
        Map<String, FieldSpecification> fields = Collections.unmodifiableMap(fieldSpecifications.stream()
                .sorted(Comparator.comparingInt(FieldSpecification::getOrder))
                .collect(Collectors.toMap(FieldSpecification::getPath, s -> s,
                        (k1, k2) -> {throw new UnsupportedOperationException("Duplicate key");}, LinkedHashMap::new)));
        return new FormSpecification(specifiedClass, method, actionScheme, fields);
    }
}
