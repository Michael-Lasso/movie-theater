package com.jpmc.theater.utils;

import com.jpmc.theater.domain.Movie;
import com.jpmc.theater.domain.Showing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ValidatorTest {

    private Validator validator;
    private Movie movie = Movie
            .builder()
            .ticketPrice(15.0)
            .title("The Matrix")
            .description("Best movie ever, convince me otherwise")
            .specialCode(5)
            .runningTime(Duration.ofMinutes(136))
            .build();

    @BeforeEach
    public void init() {
        validator = new Validator();
    }

    @Test
    void invalidShowing() {
        Showing showing1 = Showing.builder().showStartTime(LocalDateTime.now()).movie(movie).daySequence(0).build();
        Showing showing2 = Showing.builder().showStartTime(LocalDateTime.now()).daySequence(1).build();
        boolean validate1 = validator.validate(showing1);
        boolean validate2 = validator.validate(showing2);
        assertThat(validate1).isFalse();
        assertThat(validate2).isFalse();
    }

    @Test
    void invalidMovie() {
        Movie badPrice = Movie
                .builder()
                .ticketPrice(0)
                .title("The Matrix")
                .description("Best badPrice ever, convince me otherwise")
                .specialCode(5)
                .runningTime(Duration.ofMinutes(136))
                .build();
        Movie missingTitle = Movie
                .builder()
                .ticketPrice(0)
                .description("Best badPrice ever, convince me otherwise")
                .specialCode(5)
                .runningTime(Duration.ofMinutes(136))
                .build();
        boolean validate1 = validator.validate(badPrice);
        boolean validate2 = validator.validate(missingTitle);
        assertThat(validate1).isFalse();
        assertThat(validate2).isFalse();
    }

    @Test
    void validShowing() {
        Showing showing = Showing.builder().showStartTime(LocalDateTime.now()).daySequence(1).movie(movie).build();
        boolean validate = validator.validate(showing);
        assertThat(validate).isTrue();
    }

    @Test
    void validMovie() {
        boolean validate = validator.validate(movie);
        assertThat(validate).isTrue();
    }
}