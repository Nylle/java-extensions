package com.github.nylle.javaextensions;

import lombok.experimental.ExtensionMethod;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtensionMethod(StreamExtensions.class)
class StreamExtensionsTest {

    @Nested
    class Partition {

        @Test
        void isLazy() {
            var infiniteStream = Stream.iterate(0, i -> i + 1);

            var actual = infiniteStream.partition(3).skip(1).limit(3).toList();

            assertThat(actual).containsExactly(
                    List.of(3, 4, 5),
                    List.of(6, 7, 8),
                    List.of(9, 10, 11));
        }
    }

    @Nested
    class SlidingWindow {

        @Test
        void isLazy() {
            var infiniteStream = Stream.iterate(0, i -> i + 1);

            var actual = infiniteStream.slidingWindow(3).skip(1).limit(3).toList();

            assertThat(actual).containsExactly(
                    List.of(1, 2, 3),
                    List.of(2, 3, 4),
                    List.of(3, 4, 5));
        }
    }
}