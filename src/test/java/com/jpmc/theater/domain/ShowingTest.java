package com.jpmc.theater.domain;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ShowingTest {

    @Test
    void toJson() {

        Movie walle = Movie
                .builder()
                .ticketPrice(15.0)
                .title("WALL-E")
                .description("Potential candidate for second best movie")
                .specialCode(5)
                .runningTime(Duration.ofMinutes(98))
                .build();
        LocalDateTime showTime = LocalDateTime.of(2016, 3, 4, 11, 30, 0);

        Showing showing = Showing.builder().movie(walle).showStartTime(showTime).daySequence(1).build();

        String actual = showing.toJson(Optional.of(14.0));
        String expected = "\n" +
                "  {\n" +
                " \"sequenceOfTheDay\":1,\n" +
                " \"showStartTime\":\"2016-03-04T11:30\",\n" +
                "    \"movie\" :{\n" +
                "      \"title\":\"WALL-E\",\n" +
                "      \"description\":\"Potential candidate for second best movie\",\n" +
                "      \"runningTime\":\"(1 hour 38 minutes)\",\n" +
                "      \"ticketPrice\":14.0\n" +
                "      }\n" +
                "}\n";

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toJsonEmpty() {
        LocalDateTime showTime = LocalDateTime.of(2016, 3, 4, 11, 30, 0);
        Showing showing = Showing.builder().showStartTime(showTime).daySequence(1).build();

        String json = showing.toJson(Optional.empty());
        String expected ="\n" +
                "  {\n" +
                " \"sequenceOfTheDay\":1,\n" +
                " \"showStartTime\":\"2016-03-04T11:30\",\n" +
                "    \"movie\" :{\"description\":\"This movie is not available\"}\n" +
                "}\n";

        assertThat(json).isEqualTo(expected);
    }
}