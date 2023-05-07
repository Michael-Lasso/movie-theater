package com.jpmc.theater.utils;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TheaterUtilsTest {

    @Test
    void mapToTime() {
        String time = "2016-03-04 11:30";
        LocalDateTime localDateTime = TheaterUtils.mapToTime(time);
        LocalDateTime expected = LocalDateTime.of(2016, 3, 4, 11, 30, 0);
        assertThat(localDateTime).isEqualTo(expected);
    }

    @Test
    void humanReadableFormatAboveAnHour() {
        Duration duration = Duration.ofMinutes(90);
        String response = TheaterUtils.humanReadableFormat(duration);
        assertThat(response).isEqualTo("(1 hour 30 minutes)");
    }

    @Test
    void humanReadableFormatBelowAnHour() {
        Duration duration = Duration.ofMinutes(55);
        String response = TheaterUtils.humanReadableFormat(duration);
        assertThat(response).isEqualTo("(55 minutes)");
    }

    @Test
    void humanReadableFormatEmpty() {
        Duration duration = Duration.ofMinutes(0);
        String response = TheaterUtils.humanReadableFormat(duration);
        assertThat(response).isEqualTo("not available");
    }
}