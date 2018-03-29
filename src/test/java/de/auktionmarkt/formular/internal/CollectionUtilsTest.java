package de.auktionmarkt.formular.internal;

import de.auktionmarkt.formular.internal.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class CollectionUtilsTest {

    @Test(expected = UnsupportedOperationException.class)
    public void testWrapImmutableSet() {
        Set<String> data = CollectionUtils.warpImmutableSet("Hello", "World");
        try {
            Assert.assertEquals(2, data.size());
            Assert.assertTrue(data.contains("Hello"));
            Assert.assertTrue(data.contains("World"));
        } catch (Throwable e) {
            Assert.fail(e.getMessage());
        }
        data.add("Another test");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testToImmutableSet() {
        Set<String> origin = new HashSet<>();
        origin.add("Hello");
        origin.add("World");
        Set<String> data = CollectionUtils.toImmutableSet(origin);
        try {
            Assert.assertEquals(2, data.size());
            Assert.assertTrue(data.contains("Hello"));
            Assert.assertTrue(data.contains("World"));
        } catch (Throwable e) {
            Assert.fail(e.getMessage());
        }
        data.add("Another test");
    }
}
