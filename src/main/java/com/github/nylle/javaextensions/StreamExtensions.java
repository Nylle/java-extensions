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
     * Creates a lazy sliding window with {@code size} for the elements in {@code stream}.
     * <p>
     * No matter how large the window is, it will always contain only one new element, which means that the "overlap" is always {@code size}-1.
     * <p>
     * For example, a stream with the elements
     *   [1 2 3 4 5]
     * and window-size of 3 will be partitioned into
     *   [[1 2 3] [2 3 4] [3 4 5]]
     *
     * @param stream the stream to create the sliding window over
     * @param size the size of the sliding window
     * @return a stream of lists with {@code size}
     * @param <T> the type of elements in {@code stream}
     */
    public static <T> Stream<List<T>> slidingWindow(Stream<T> stream, int size) {
        Queue<T> queue = new LinkedList<>();
        return stream
                .dropWhile(item -> queue.size() < size - 1 && queue.add(item))
                .map(item -> {
                    queue.add(item);
                    var result = queue.stream().toList();
                    queue.remove();
                    return result;
                });
    }

    /**
     * Partitions {@code stream} into a {@link Stream} of lists with {@code size}.
     * <p>
     * For example, a stream with the elements
     *   [1 2 3 4 5]
     * using a partition size of 2 will be transformed into
     *   [[1 2] [3 4] [5]]
     *
     * @param stream the stream to partition
     * @param size the maximum size of each partition
     * @return a stream of lists containing the elements of {@code stream}
     * @param <T> the type of elements in {@code stream}
     */
    public static <T> Stream<List<T>> partition(Stream<T> stream, int size) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new BatchIterator<>(stream.iterator(), size), ORDERED), false);
    }

    @RequiredArgsConstructor
    private static class BatchIterator<T> implements Iterator<List<T>> {
        private final Iterator<T> iterator;
        private final int batchSize;
        private List<T> currentBatch;

        @Override
        public List<T> next() {
            prepareNextBatch();
            return currentBatch;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        private void prepareNextBatch() {
            currentBatch = new ArrayList<>(batchSize);
            while (iterator.hasNext() && currentBatch.size() < batchSize) {
                currentBatch.add(iterator.next());
            }
        }
    }
}