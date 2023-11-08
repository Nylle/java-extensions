package com.github.nylle.javaextensions;

import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class InstantExtensions {

    /**
     * Formats the provided {@link Instant} using the provided pattern for the provided {@link ZoneId}.
     *
     * @param instant the instant to format
     * @param pattern the pattern as used by {@link DateTimeFormatter}
     * @param zoneId the zoneId
     * @return the formatted date and time according to the pattern
     */
    public static String format(Instant instant, String pattern, ZoneId zoneId) {
        return DateTimeFormatter.ofPattern(pattern).withZone(zoneId).format(instant);
    }

    /**
     * Formats the provided {@link Instant} using the provided pattern and {@link ZoneId#systemDefault}.
     *
     * @param instant the instant to format
     * @param pattern the pattern as used by {@link DateTimeFormatter}
     * @return the formatted date and time according to the pattern
     */
    public static String format(Instant instant, String pattern) {
        return format(instant, pattern, ZoneId.systemDefault());
    }
}
