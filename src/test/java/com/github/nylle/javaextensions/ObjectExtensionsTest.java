package com.github.nylle.javaextensions;

import lombok.experimental.ExtensionMethod;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtensionMethod({ObjectExtensions.class})
class ObjectExtensionsTest {

    @Nested
    class Let {

        @Test
        void maps() {
            var actual = "hello".let(x -> x.toUpperCase());

            assertThat(actual).isEqualTo("HELLO");
        }

        @Test
        void isNullSafe() {
            String nullable = null;

            var actual = nullable.let(x -> x.toUpperCase());

            assertThat(actual).isNull();
        }
    }

    @Nested
    class With {

        @Test
        void maps() {
            var actual = "hello".with(x -> x.toUpperCase());

            assertThat(actual).isEqualTo("HELLO");
        }

        @Test
        void isNotNullSafe() {
            String nullable = null;

            assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> nullable.with(x -> x.toUpperCase()));
        }
    }

    @Nested
    class Or {

        @Test
        void returnsNullableIfNotNull() {
            assertThat("not null".or("other")).isEqualTo("not null");
        }

        @Test
        void returnsOtherIfNull() {
            String nullable = null;

            assertThat(nullable.or("other")).isEqualTo("other");
        }

        @Test
        void invokesSupplierIfNull() {
            String nullable = null;

            assertThat(nullable.or(() -> "other")).isEqualTo("other");
        }
    }

    @Nested
    class Also {

        @Test
        void appliesSideEffect() {
            var actual = new ArrayList<String>();

            var result = "hello".also(x -> actual.add(x));

            assertThat(result).isEqualTo("hello");
            assertThat(actual).containsExactly("hello");
        }

        @Test
        void isNotNullSafe() {
            String nullable = null;

            var actual = new ArrayList<String>();

            var result = nullable.also(x -> actual.add(x));

            assertThat(result).isNull();
            assertThat(actual).hasSize(1);
            assertThat(actual.get(0)).isNull();
        }
    }

    @Nested
    class Optional {

        @Test
        void createsOptional() {
            var actual = "hello".optional();

            assertThat(actual).hasValue("hello");
        }

        @Test
        void createsEmptyOptional() {
            String nullable = null;

            var actual = nullable.optional();

            assertThat(actual).isEmpty();
        }
    }
}