package de.auktionmarkt.formular.internal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectionUtils {

    @SafeVarargs
    public static <T> Set<T> warpImmutableSet(T... data) {
        return toImmutableSet(Arrays.asList(data));
    }

    public static <T> Set<T> toImmutableSet(Iterable<T> data) {
        if (data instanceof Collection) {
            Collection<T> collection = (Collection<T>) data;
            if (collection.isEmpty())
                return Collections.emptySet();
            return Collections.unmodifiableSet(new HashSet<>((Collection<T>) data));
        } else {
            Set<T> set = new HashSet<>();
            for (T entry : data)
                set.add(entry);
            return !set.isEmpty() ? Collections.unmodifiableSet(set) : Collections.emptySet();
        }
    }
}
