package com.github.nylle.javaextensions;

import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ListExtensions {

    /**
     * Creates a new {@link List} containing all elements of {@code list} and {@code lists}.
     *
     * @param list the list to be concatenated
     * @param lists the lists to be concatenated to {@code list}
     * @return a new list containing all elements of {@code list} and {@code lists}
     * @param <T> the type of elements in {@code list} and {@code lists}
     */
    @SafeVarargs
    public static <T> List<T> concat(List<T> list, List<T>... lists) {
        return Stream.concat(list.stream(), Arrays.stream(lists).flatMap(x -> x.stream())).toList();
    }

    /**
     * Finds the element at {@code index} of {@code list}.
     * <p>
     * If the index does not exist, the result is {@code null}.
     *
     * @param list the list to be searched
     * @param index the index to be found
     * @return the value at index or null
     * @param <T> the type of elements in {@code list}
     */
    public static <T> T find(List<T> list, int index) {
        return list.stream().skip(index).findFirst().orElse(null);
    }

    /**
     * Creates a new {@link List} with {@code size} containing all elements of {@code list} padded with {@code value}.
     * <p>
     * If the provided list has the same or greater size than {@code size}, it will be returned as is.
     *
     * @param list the list to be padded
     * @param size  the size to pad to
     * @param value the value to use for additional elements
     * @return a new list with {@code size} or greater
     * @param <T> the type of elements in {@code list}
     */
    public static <T> List<T> pad(List<T> list, int size, T value) {
        return list.size() < size
                ? Stream.concat(list.stream(), Stream.iterate(0, i -> i + 1).limit((long)size - list.size()).map(x -> value)).toList()
                : list;
    }

    /**
     * Creates a new {@link List} containing all elements of {@code list} with {@code element} appended.
     *
     * @param list the list to append {@code element} to
     * @param element the element to add to {@code list}
     * @return a new list containing all elements of {@code list} and {@code element}
     * @param <T> the type of elements in {@code list}
     */
    public static <T> List<T> append(List<T> list, T element) {
        return Stream.concat(list.stream(), Stream.of(element)).toList();
    }

    /**
     * Applies {@code mapper} to each element in {@code list}.
     *
     * @param list the list to iterate over
     * @param mapper the mapper to apply to each element
     * @return a new {@link List} with same size as {@code list} containing the mapping results
     * @param <T> the type of elements in {@code list}
     * @param <R> the type of elements in returned {@link List}
     */
    public static <T, R> List<R> map(List<T> list, Function<T, R> mapper) {
        return list.stream().map(mapper).toList();
    }

    /**
     * Filters {@code list} by applying {@code predicate} to each element.
     *
     * @param <T>       the type of elements in {@code list}
     * @param list      the list to iterate over
     * @param predicate the predicate by which to filter
     * @return a new {@link List} filtered list
     */
    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        return list.stream().filter(predicate).toList();
    }

    /**
     * Creates a {@link Map} from the elements of {@code list} by applying {@code keyMapper} and {@code valueMapper}.
     *
     * @param list the list to iterate over
     * @param keyMapper the mapper to apply to each element in {@code list} to determine the key
     * @param valueMapper the mapper to apply to each element in {@code list} to determine the value
     * @return a new {@link Map} based on the elements in {@code list}
     * @param <T> the type of elements in {@code list}
     * @param <K> the type of the keys in the result
     * @param <V> the type of the values in the result
     */
    public static <T, K, V> Map<K, V> toMap(List<T> list, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return list.stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }

    /**
     * Creates a {@link Map} from the elements of {@code list} by applying {@code keyMapper} and {@code valueMapper}
     * as well as the type of elements in {@code mergeFunction} in case of collisions.
     *
     * @param list the list to iterate over
     * @param keyMapper the mapper to apply to each element in {@code list} to determine the key
     * @param valueMapper the mapper to apply to each element in {@code list} to determine the value
     * @param mergeFunction the function to apply in case of duplicate keys
     * @return a new {@link Map} based on the elements in {@code list}
     * @param <T> the type of elements in {@code list}
     * @param <K> the type of the keys in the result
     * @param <V> the type of the values in the result
     */
    public static <T, K, V> Map<K, V> toMap(List<T> list, Function<T, K> keyMapper, Function<T, V> valueMapper, BinaryOperator<V> mergeFunction) {
        return list.stream().collect(Collectors.toMap(keyMapper, valueMapper, mergeFunction));
    }
}
