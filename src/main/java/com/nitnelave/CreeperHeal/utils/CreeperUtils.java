package com.nitnelave.CreeperHeal.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A utility class for common tasks used in several places.
 * 
 * @author nitnelave
 * 
 */
public abstract class CreeperUtils {

    /**
     * Concatenate two arrays.
     * 
     * @param first
     *            The first array.
     * @param second
     *            The second array
     * @return An array containing the elements of both arrays in the correct
     *         order.
     */
    public static <T> T[] concat (T[] first, T[] second) {
        T[] result = Arrays.copyOf (first, first.length + second.length);
        System.arraycopy (second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Create a final HashSet from a collection of elements.
     * 
     * @param elements
     *            The elements to be added in the HashSet.
     * @return An unmodifiable HashSet containing the elements.
     */
    public static <T> Set<T> createFinalHashSet (T... elements) {
        Set<T> set = new HashSet<T> (elements.length);

        for (T element : elements)
            set.add (element);

        return Collections.unmodifiableSet (set);
    }

}
