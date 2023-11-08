package com.github.nylle.javaextensions;

import lombok.experimental.ExtensionMethod;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

@ExtensionMethod(InstantExtensions.class)
class InstantExtensionsTest {

    @Nested
    class Format {

        @Test
        void formatsWithZoneId() {
            var actual = Instant.ofEpochSecond(0).format("yyyy-MM-dd HH:mm:ss", ZoneId.of("Europe/Berlin"));

            assertThat(actual).isEqualTo("1970-01-01 01:00:00");
        }

        @Test
        void formatsWithDefaultZoneId() {
            var actual = Instant.ofEpochSecond(0).format("yyyy-MM-dd HH:mm:ss");
            var expected = Instant.ofEpochSecond(0).format("yyyy-MM-dd HH:mm:ss", ZoneId.systemDefault());

            assertThat(actual).isEqualTo(expected);
        }
    }
}