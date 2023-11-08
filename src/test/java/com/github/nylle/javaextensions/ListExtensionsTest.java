package com.github.nylle.javaextensions;

import lombok.experimental.ExtensionMethod;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.entry;

@ExtensionMethod(ListExtensions.class)
class ListExtensionsTest {

    @Nested
    class Concat {

        @Test
        void returnsNewImmutableListContainingElementsOfProvidedLists() {
            var actual = List.of("foo").concat(List.of("bar"), List.of("bat"));

            assertThat(actual).containsExactly("foo", "bar", "bat");
        }
    }

    @Nested
    class Find {

        @Test
        void returnsNthElementInList() {
            var list = List.of("foo", "bar");

            var actual = list.find(1);

            assertThat(actual).isEqualTo("bar");
        }

        @Test
        void returnsEmptyIfNthElementNotFound() {
            var list = List.of("foo");

            var actual = list.find(1);

            assertThat(actual).isNull();
        }
    }

    @Nested
    class Pad {
        @Test
        void appendsSpecifiedValueToGivenListUntilSpecifiedSizeIsReached() {
            var list = List.of("foo", "bar");

            var actual = list.pad(4, "baz");

            assertThat(actual).containsExactly("foo", "bar", "baz", "baz");
        }

        @Test
        void returnsGivenListIfListSizeIsSameAsSpecifiedSize() {
            var list = List.of("foo", "bar");

            var actual = list.pad(2, "baz");

            assertThat(actual).containsExactly("foo", "bar");
        }

        @Test
        void returnsGivenListIfListSizeExceedsSpecifiedSize() {
            var list = List.of("foo", "bar");

            var actual = list.pad(1, "baz");

            assertThat(actual).containsExactly("foo", "bar");
        }
    }

    @Nested
    class Append {

        @Test
        void returnsNewImmutableListWithElementsOfProvidedImmutableListAndProvidedElement() {
            var list = List.of("foo");

            var actual = list.append("bar");

            assertThat(actual).containsExactly("foo", "bar");
        }
    }

    @Nested
    class Map {

        @Test
        void mapsAllElementsInList() {
            var list = List.of("foo", "bar");

            var actual = list.map(x -> x.toUpperCase());

            assertThat(actual).containsExactly("FOO", "BAR");
        }
    }

    @Nested
    class Filter {

        @Test
        void filtersElementsInList() {
            var list = List.of("foo", "bar");

            var actual = list.filter(x -> x.equals("bar"));

            assertThat(actual).containsExactly("bar");
        }
    }

    @Nested
    class ToMap {

        @Test
        void convertsListToMap() {
            var list = List.of(java.util.Map.entry("foo", 1), java.util.Map.entry("bar", 2));

            var actual = list.toMap(k -> k.getKey(), v -> v.getValue());

            assertThat(actual).containsExactlyInAnyOrderEntriesOf(java.util.Map.of("foo", 1, "bar", 2));
        }

        @Test
        void throwsOnCollision() {
            var list = List.of(java.util.Map.entry("foo", 1), java.util.Map.entry("foo", 2));

            assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> list.toMap(k -> k.getKey(), v -> v.getValue()))
                    .withMessage("Duplicate key foo (attempted merging values 1 and 2)");
        }

        @Test
        void mergesOnCollision() {
            var list = List.of(java.util.Map.entry("foo", 1), java.util.Map.entry("foo", 2));

            var actual = list.toMap(k -> k.getKey(), v -> v.getValue(), (a, b) -> b);

            assertThat(actual).containsExactly(entry("foo", 2));
        }
    }
}