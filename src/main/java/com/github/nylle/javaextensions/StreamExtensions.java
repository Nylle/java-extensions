package com.github.nylle.javaextensions;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Spliterator.ORDERED;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class StreamExtensions {

    /**
     * Partitions {@code stream} into a {@link Stream} of lists with {@code size}.
     * <p>
     * For example, a stream with the elements
     * [1 2 3 4 5]
     * using a partition size of 2 will be partitioned into
     * [[1 2] [3 4]]
     * while items that do not make a complete partition are being dropped.
     *
     * @param stream the stream to partition
     * @param size   the maximum size of each partition
     * @param <T>    the type of elements in {@code stream}
     * @return a stream of lists containing the elements of {@code stream}
     */
    public static <T> Stream<List<T>> partition(Stream<T> stream, int size) {
        return partition(stream, size, size);
    }

    /**
     * Partitions {@code stream} into a {@link Stream} of lists with {@code size} at offsets {@code step} apart.
     * <p>
     * For example, a stream with the elements
     * [1 2 3 4 5 6 7 8 9]
     * using a partition size of 2, a step of 4 will be partitioned into
     * [[1 2] [5 6]]
     * while items that do not make a complete partition are being dropped.
     * <p>
     * If {@code step} is smaller than {@code size}, elements are being re-used.
     * For example, a stream with the elements
     * [1 2 3 4 5]
     * using a partition size of 3 and a step of 1 will be partitioned into
     * [[1 2 3] [2 3 4] [3 4 5]]
     *
     * @param stream the stream to partition
     * @param size   the maximum size of each partition
     * @param step   the number of elements between the start of each partition
     * @param <T>    the type of elements in {@code stream}
     * @return a stream of lists containing the elements of {@code stream}
     */
    public static <T> Stream<List<T>> partition(Stream<T> stream, int size, int step) {
        return partition(stream, size, step, null);
    }

    /**
     * Partitions {@code stream} into a {@link Stream} of lists with {@code size} at offsets {@code step} apart
     * padding with {@code pad} as necessary to complete the last partition.
     * <p>
     * For example, a stream with the elements
     * [1 2 3 4 5 6 7 8 9]
     * using a partition size of 2, a step of 4, and a pad of [0] will be partitioned into
     * [[1 2] [5 6] [9 0]]
     * <p>
     * If {@code pad} contains not enough elements, the last partition may be shorter than {@code size}.
     * For example, a stream with the elements
     * [1 2 3 4 5 6 7]
     * using a partition size of 3, a step of 3, and a pad of [0] will be partitioned into
     * [[1 2 3] [4 5 6] [7 0]]
     * <p>
     * If {@code pad} contains too many elements, they will be discarded beyond {@code size}.
     * For example, a stream with the elements
     * [1 2 3 4 5 6 7]
     * using a partition size of 3, a step of 3, and a pad of [0 0 0] will be partitioned into
     * [[1 2 3] [4 5 6] [7 0 0]]
     * <p>
     * If {@code step} is smaller than {@code size}, elements are being re-used.
     * For example, a stream with the elements
     * [1 2 3 4 5]
     * using a partition size of 3 and a step of 1 will be partitioned into
     * [[1 2 3] [2 3 4] [3 4 5]]
     *
     * @param stream the stream to partition
     * @param size   the maximum size of each partition
     * @param step   the number of elements between the start of each partition
     * @param <T>    the type of elements in {@code stream}
     * @return a stream of lists containing the elements of {@code stream}
     */
    public static <T> Stream<List<T>> partition(Stream<T> stream, int size, int step, List<T> pad) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new BatchIterator<>(stream.iterator(), size, step, pad), ORDERED), false).filter(x -> !x.isEmpty());
    }

    /**
     * Creates a lazy sliding window with {@code size} for the elements in {@code stream}.
     * <p>
     * No matter how large the window is, it will always contain only one new element, which means that the "overlap" is always {@code size}-1.
     * <p>
     * For example, a stream with the elements
     * [1 2 3 4 5]
     * and window-size of 3 will be partitioned into
     * [[1 2 3] [2 3 4] [3 4 5]]
     *
     * @param stream the stream to create the sliding window over
     * @param size   the size of the sliding window
     * @param <T>    the type of elements in {@code stream}
     * @return a stream of lists with {@code size}
     */
    public static <T> Stream<List<T>> slidingWindow(Stream<T> stream, int size) {
        return partition(stream, size, 1);
    }

    private static Stream<Integer> range(int exclusive) {
        return Stream.iterate(0, i -> i + 1).limit(exclusive);
    }

    @RequiredArgsConstructor
    private static class BatchIterator<T> implements Iterator<List<T>> {
        private final Iterator<T> iterator;
        private final int batchSize;
        private final int step;
        private final List<T> pad;
        private final Queue<T> queued = new LinkedList<>();
        private List<T> discarded = new ArrayList<>();

        @Override
        public List<T> next() {
            prepareNextPartition();
            dropItems(step - batchSize);

            var partition = queued.stream().limit(batchSize).toList();

            if (pad == null && partition.size() < batchSize) {
                return List.of();
            }

            return partition;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        private void prepareNextPartition() {
            cleanUpQueue();
            while (iterator.hasNext() && queued.size() < batchSize) {
                queued.add(iterator.next());
            }
            padQueue();
        }

        private void padQueue() {
            if (queued.size() < batchSize && pad != null) {
                queued.addAll(pad);
            }
        }

        private void cleanUpQueue() {
            if (queued.isEmpty()) {
                return;
            }

            if (step > batchSize) {
                queued.clear();
                return;
            }

            range(step).forEach(i -> queued.remove());
        }

        private void dropItems(int count) {
            while (iterator.hasNext() && discarded.size() < count) {
                discarded.add(iterator.next());
            }
            discarded = new ArrayList<>();
        }
    }
}