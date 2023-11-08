package com.github.nylle.javaextensions;

import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class MapExtensions {

    /**
     * Creates a {@link Map} representing the union of {@code left} and {@code right}.
     * <p>
     * The returned map contains all keys from the two provided maps with the corresponding value being a {@link Tuple}
     * containing the matching values from both lists or empty.
     * <p>
     * In set theory, the union of a collection of sets is the set of all elements in the collection.
     *
     * @param left a map to be included in the union
     * @param right a map to be included in the union
     * @return a map containing all elements of the provided maps
     * @param <K> the type of keys maintained by this map
     * @param <V> the type of mapped values
     */
    public static <K, V> Map<K, Tuple<V>> union(Map<K, V> left, Map<K, V> right) {
        return Stream.of(left.keySet(), right.keySet())
                .flatMap(x -> x.stream())
                .distinct()
                .collect(toMap(k -> k, v -> new Tuple<>(left.get(v), right.get(v))));
    }

    public record Tuple<V>(V left, V right) { }
}
