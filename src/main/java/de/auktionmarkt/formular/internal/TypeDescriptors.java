package de.auktionmarkt.formular.internal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.convert.TypeDescriptor;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TypeDescriptors {

    public static final TypeDescriptor STRING_TYPE = TypeDescriptor.valueOf(String.class);
    public static final TypeDescriptor STRING_LIST = TypeDescriptor.collection(List.class, STRING_TYPE);
}
