package com.github.nylle.javaextensions;

import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ObjectExtensions {

    /**
     * Creates an {@link Optional} from nullable {@code object}.
     *
     * @param object the nullable object
     * @return an {@link Optional}
     * @param <T> the type of {@code object}
     */
    public static <T> Optional<T> optional(T object) {
        return Optional.ofNullable(object);
    }

    /**
     * Maps {@code object} if not null using {@code mapper} and returns mapping-result otherwise returns null.
     * <p>
     * This operation is null-safe. If {@code object} is null the mapper will not be called, instead null will be returned.
     *
     * @param object the object to map
     * @param mapper the mapper to apply to {@code object}
     * @return the mapping result or null
     * @param <T> the type of {@code object}
     * @param <R> the type of the mapping result
     */
    public static <T, R> R let(T object, Function<T, R> mapper) {
        return Optional.ofNullable(object).map(mapper).orElse(null);
    }

    /**
     * Maps nullable {@code object} using {@code mapper}.
     * <p>
     * This operation is not null-safe! If {@code object} is null, mapper will be called nonetheless.
     *
     * @param object the nullable object to map
     * @param mapper the mapper to apply to {@code object}
     * @return the mapping result
     * @param <T> the type of {@code object}
     * @param <R> the type of the mapping result
     * @throws NullPointerException when trying to access null {@code object}
     */
    public static <T, R> R with(T object, Function<T, R> mapper) {
        return mapper.apply(object);
    }

    /**
     * Returns {@code object} if not null, otherwise {@code other}.
     *
     * @param object the nullable object
     * @param other the alternative object
     * @return the {@code object} if not null, otherwise {@code other}
     * @param <T> the type of {@code object}
     */
    public static <T> T or(T object, T other) {
        return object != null ? object : other;
    }

    /**
     * Returns {@code object} if not null, otherwise what {@code supplier} supplies.
     *
     * @param object the nullable object
     * @param supplier the supplier for an alternative object
     * @return the {@code object} if not null, otherwise what {@code supplier} supplies
     * @param <T> the type of {@code object}
     */
    public static <T> T or(T object, Supplier<T> supplier) {
        return object != null ? object : supplier.get();
    }

    /**
     * Produces a side effect by calling {@code consumer} with non-nullable {@code object} and returns {@code object}.
     *
     * @param object the non-nullable object
     * @param consumer the consumer to be called with {@code object}
     * @return the {@code object}
     * @param <T> the type of {@code object}
     */
    public static <T> T also(T object, Consumer<T> consumer) {
        consumer.accept(object);
        return object;
    }
}
