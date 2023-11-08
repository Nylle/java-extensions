package com.github.nylle.javaextensions;

import lombok.experimental.ExtensionMethod;
import org.junit.jupiter.api.DisplayName;
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
        @DisplayName("returns a lazy stream of lists of n items")
        void returnsALazyStreamOfListsOfNItems() {
            assertThat(Stream.iterate(0, i -> i + 1).partition(2).skip(1).limit(3).toList()).containsExactly(
                    List.of(2, 3),
                    List.of(4, 5),
                    List.of(6, 7));
        }

        @Test
        @DisplayName("drops items that make not a complete partition")
        void dropsItemsThatMakeNotACompletePartition() {
            assertThat(Stream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).partition(4).toList()).containsExactly(
                    List.of(0, 1, 2, 3),
                    List.of(4, 5, 6, 7));
        }

        @Test
        @DisplayName("uses step to select the starting point for each partition")
        void usesStepToSelectTheStartingPointForEachPartition() {
            assertThat(Stream.iterate(0, i -> i + 1).partition(4, 6).limit(3).toList()).containsExactly(
                    List.of(0, 1, 2, 3),
                    List.of(6, 7, 8, 9),
                    List.of(12, 13, 14, 15));
        }

        @Test
        @DisplayName("re-uses items if step is smaller than partition size")
        void reUsesItemsIfStepIsSmallerThanPartitionSize() {
            assertThat(Stream.iterate(0, i -> i + 1).partition(3, 2).limit(4).toList()).containsExactly(
                    List.of(0, 1, 2),
                    List.of(2, 3, 4),
                    List.of(4, 5, 6),
                    List.of(6, 7, 8));
        }

        @Test
        @DisplayName("pads last partition if not complete")
        void padsLastPartitionIfNotComplete() {
            assertThat(Stream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).partition(3, 4, List.of(0)).toList()).containsExactly(
                    List.of(0, 1, 2),
                    List.of(4, 5, 6),
                    List.of(8, 9, 0));
        }

        @Test
        @DisplayName("returns a shorter last partition if the padding is not long enough")
        void returnsAShorterLastPartitionIfThePaddingIsNotLongEnough() {
            assertThat(Stream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).partition(3, 4, List.of()).toList()).containsExactly(
                    List.of(0, 1, 2),
                    List.of(4, 5, 6),
                    List.of(8, 9));
        }

        @Test
        @DisplayName("pads last partition if not complete discarding overflow")
        void padsLastPartitionIfNotCompleteDiscardingOverflow() {
            assertThat(Stream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).partition(3, 4, List.of(0, 0, 0, 0)).toList()).containsExactly(
                    List.of(0, 1, 2),
                    List.of(4, 5, 6),
                    List.of(8, 9, 0));
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