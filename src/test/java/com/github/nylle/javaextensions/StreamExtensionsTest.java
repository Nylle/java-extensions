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
    class Range {

        @Test
        @DisplayName("range() returns an infinite integer stream")
        void returnsAnInfiniteIntegerStream() {
            var actual = StreamExtensions.range();

            assertThat(actual.limit(3).toList()).containsExactly(0, 1, 2);
        }

        @Test
        @DisplayName("range(end) returns a stream from inclusive zero to exclusive end")
        void returnsAStreamFromInclusiveZeroToExclusiveEnd() {
            Integer end = 3;

            var actual = end.range();

            assertThat(actual.toList()).containsExactly(0, 1, 2);
        }

        @Test
        @DisplayName("range(start, end) returns a stream from inclusive start to exclusive end")
        void returnsAStreamFromInclusiveStartToExclusiveEnd() {
            Integer start = -1;
            var end = 4;

            var actual = start.range(end);

            assertThat(actual.toList()).containsExactly(-1, 0, 1, 2, 3);
        }

        @Test
        @DisplayName("range(start, end, step) returns a stream from inclusive start to exclusive end using step")
        void returnsAStreamFromInclusiveStartToExclusiveEndWithStep() {
            Integer start = 3;
            var end = 10;
            var step = 3;

            var actual = start.range(end, step);

            assertThat(actual.toList()).containsExactly(3, 6, 9);
        }
    }

    @Nested
    @DisplayName("partition(stream, n)")
    class Partition {

        @Test
        @DisplayName("returns a lazy stream of lists of n items in stream")
        void returnsALazyStreamOfListsOfNItems() {
            assertThat(Stream.iterate(0, i -> i + 1).partition(2).skip(1).limit(3).toList()).containsExactly(
                    List.of(2, 3),
                    List.of(4, 5),
                    List.of(6, 7));
        }

        @Test
        @DisplayName("drops items from stream that make not a complete partition with size n")
        void dropsItemsThatMakeNotACompletePartition() {
            assertThat(Stream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).partition(4).toList()).containsExactly(
                    List.of(0, 1, 2, 3),
                    List.of(4, 5, 6, 7));
        }
    }

    @Nested
    @DisplayName("partition(stream, n, step)")
    class PartitionWithStep {

        @Test
        @DisplayName("uses step to select the starting point for each partition of n items in stream")
        void usesStepToSelectTheStartingPointForEachPartition() {
            assertThat(Stream.iterate(0, i -> i + 1).partition(4, 6).limit(3).toList()).containsExactly(
                    List.of(0, 1, 2, 3),
                    List.of(6, 7, 8, 9),
                    List.of(12, 13, 14, 15));
        }

        @Test
        @DisplayName("re-uses items in stream if step is smaller than n")
        void reUsesItemsIfStepIsSmallerThanPartitionSize() {
            assertThat(Stream.iterate(0, i -> i + 1).partition(3, 2).limit(4).toList()).containsExactly(
                    List.of(0, 1, 2),
                    List.of(2, 3, 4),
                    List.of(4, 5, 6),
                    List.of(6, 7, 8));
        }

        @Test
        @DisplayName("can be used to implement a lazy sliding window")
        void canBeUsedToGetASlidingWindow() {
            assertThat(Stream.iterate(0, i -> i + 1).partition(2, 1).limit(4).toList()).containsExactly(
                    List.of(0, 1),
                    List.of(1, 2),
                    List.of(2, 3),
                    List.of(3, 4));
        }
    }

    @Nested
    @DisplayName("partition(stream, n, step, pad)")
    class PartitionWithStepAndPad {

        @Test
        @DisplayName("pads last partition of items in stream if shorter than n")
        void padsLastPartitionIfNotComplete() {
            assertThat(Stream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).partition(3, 4, List.of(0)).toList()).containsExactly(
                    List.of(0, 1, 2),
                    List.of(4, 5, 6),
                    List.of(8, 9, 0));
        }

        @Test
        @DisplayName("returns a shorter last partition of items in stream if pad is not long enough to pad to n")
        void returnsAShorterLastPartitionIfThePaddingIsNotLongEnough() {
            assertThat(Stream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).partition(3, 4, List.of()).toList()).containsExactly(
                    List.of(0, 1, 2),
                    List.of(4, 5, 6),
                    List.of(8, 9));
        }

        @Test
        @DisplayName("discards surplus elements in pad, if last partition of items in stream is successfully padded to n")
        void padsLastPartitionIfNotCompleteDiscardingOverflow() {
            assertThat(Stream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).partition(3, 4, List.of(0, 0, 0, 0)).toList()).containsExactly(
                    List.of(0, 1, 2),
                    List.of(4, 5, 6),
                    List.of(8, 9, 0));
        }

        @Test
        @DisplayName("can be used for batching with the last batch being potentially shorter")
        void canBeUsedForBatchingWithTheLastBatchBeingPotentiallyShorter() {
            var batchSize = 4;

            assertThat(Stream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).partition(batchSize, batchSize, List.of()).toList()).containsExactly(
                    List.of(0, 1, 2, 3),
                    List.of(4, 5, 6, 7),
                    List.of(8, 9));
        }
    }

    @Nested
    @DisplayName("zip(stream, other, f)")
    class Zip {

        @Test
        @DisplayName("returns a lazy stream consisting of the result of applying f to the set of nth items of each input stream")
        void isLazy() {
            var stream = Stream.iterate(0, i -> i + 1);
            var other = Stream.iterate(0, i -> i + 1);

            var actual = stream.zip(other, (a, b) -> a + b).skip(10).limit(3).toList();

            assertThat(actual).containsExactly(20, 22, 24);
        }

        @Test
        @DisplayName("maps items until stream runs out")
        void withStreamShorterThanOther() {
            var stream = Stream.of("kung", "wunder");
            var other = Stream.of("foo", "bar", "ignored");

            var actual = stream.zip(other, (a, b) -> a + b).toList();

            assertThat(actual).containsExactly("kungfoo", "wunderbar");
        }

        @Test
        @DisplayName("maps items until other runs out")
        void withOtherShorterThanStream() {
            var stream = Stream.of("kung", "wunder", "ignored");
            var other = Stream.of("foo", "bar");

            var actual = stream.zip(other, (a, b) -> a + b).toList();

            assertThat(actual).containsExactly("kungfoo", "wunderbar");
        }
    }
}