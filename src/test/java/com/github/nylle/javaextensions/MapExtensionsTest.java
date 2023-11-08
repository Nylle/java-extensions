package com.github.nylle.javaextensions;

import com.github.nylle.javaextensions.MapExtensions.Tuple;
import lombok.experimental.ExtensionMethod;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtensionMethod(MapExtensions.class)
class MapExtensionsTest {

    @Nested
    class Union {

        @Test
        void returnsValuesForKeysPresentInBothMaps() {
            var left = Map.of(
                    "1", "foo",
                    "2", "left");
            var right = Map.of(
                    "1", "bar",
                    "2", "right");

            var actual = left.union(right);

            var expected = Map.of(
                    "1", new Tuple<>("foo", "bar"),
                    "2", new Tuple<>("left", "right"));

            assertThat(actual).containsExactlyInAnyOrderEntriesOf(expected);
        }

        @Test
        void returnsNullForKeysPresentInOneMap() {
            var left = Map.of("1", "left");
            var right = Map.of("2", "right");

            var actual = left.union(right);

            var expected = Map.of(
                    "1", new Tuple<>("left", null),
                    "2", new Tuple<>(null, "right"));

            assertThat(actual).containsExactlyInAnyOrderEntriesOf(expected);
        }
    }

    @Nested
    class ATuple {
        @Test
        void holdsTheProvidedValues() {
            var actual = new Tuple<>("left", "right");

            assertThat(actual.left()).isEqualTo("left");
            assertThat(actual.right()).isEqualTo("right");
        }

        @Test
        void canHaveNullValues() {
            var actual = new Tuple<>((String) null, null);

            assertThat(actual.left()).isNull();
            assertThat(actual.right()).isNull();
        }
    }
}