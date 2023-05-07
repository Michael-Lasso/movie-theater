package com.jpmc.theater.domain;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class MovieTests {

    @Test
    void toJson() {

        Movie movie = Movie
                .builder()
                .ticketPrice(15.0)
                .title("The Matrix")
                .description("Best movie ever, convince me otherwise")
                .specialCode(5)
                .runningTime(Duration.ofMinutes(136))
                .build();

        String actual = movie.toJson(Optional.empty());
        String expected = "{\n" +
                "      \"title\":\"The Matrix\",\n" +
                "      \"description\":\"Best movie ever, convince me otherwise\",\n" +
                "      \"runningTime\":\"(2 hours 16 minutes)\",\n" +
                "      \"ticketPrice\":15.0\n" +
                "      }";

        assertThat(actual).isEqualTo(expected);
    }
}
