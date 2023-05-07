package com.jpmc.theater.services;

import com.jpmc.theater.domain.Customer;
import com.jpmc.theater.domain.Movie;
import com.jpmc.theater.domain.MovieTheaterException;
import com.jpmc.theater.domain.Showing;
import com.jpmc.theater.utils.TheaterUtils;
import com.jpmc.theater.utils.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Function;

import static com.jpmc.theater.data.DiscountRules.SEVENTH_DAY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscountServiceTest {

    private DiscountService discountService;
    @Mock
    private Validator validator;

    @BeforeEach
    void init() {
        discountService = new DiscountService(validator);
        discountService.loadDiscountRules();
    }

    @Test
    void insertNullDiscountRule() {
        assertThat(discountService.discountRuleSize()).isEqualTo(5);
        boolean someEmptyRule = discountService.addDiscountRule("some empty rule", null);
        assertThat(someEmptyRule).isFalse();
        assertThat(discountService.discountRuleSize()).isEqualTo(5);
    }

    @Test
    void insertDuplicateDiscountRule() {
        Function<Showing, Double> seventhFunction = showing -> 0.0;
        assertThat(discountService.discountRuleSize()).isEqualTo(5);
        boolean someEmptyRule = discountService.addDiscountRule(SEVENTH_DAY, seventhFunction);
        assertThat(someEmptyRule).isFalse();
        assertThat(discountService.discountRuleSize()).isEqualTo(5);
    }


    @Test
    void discountSpecialMovieAndNotSeventhDay() {
        String startTime = "2016-03-07 18:30";
        when(validator.validate(any(Showing.class))).thenReturn(Boolean.TRUE);
        Movie movie = Movie.builder().ticketPrice(10).specialCode(TheaterUtils.MOVIE_CODE_SPECIAL).build();
        Showing showing = Showing.builder().movie(movie).daySequence(3).showStartTime(TheaterUtils.mapToTime(startTime)).build();
        double calculatedPrice = discountService.calculateDiscountPrice(showing);
        double expectedPrice = 8.0;
        assertThat(calculatedPrice).isEqualTo(expectedPrice);
    }

    @Test
    void discountFirstShowAndNotEarlyBird() {
        String startTime = "2016-03-04 11:30";
        Movie movie = Movie.builder().ticketPrice(10).build();
        when(validator.validate(any(Showing.class))).thenReturn(Boolean.TRUE);
        Showing showing = Showing.builder().movie(movie).daySequence(1).showStartTime(TheaterUtils.mapToTime(startTime)).build();
        double calculatedPrice = discountService.calculateDiscountPrice(showing);
        double expectedPrice = 7.0;
        assertThat(calculatedPrice).isEqualTo(expectedPrice);
    }

    @Test
    void discountSecondShow() {
        String startTime = "2016-03-04 18:30";
        Movie movie = Movie.builder().ticketPrice(10).build();
        when(validator.validate(any(Showing.class))).thenReturn(Boolean.TRUE);
        Showing showing = Showing.builder().movie(movie).daySequence(2).showStartTime(TheaterUtils.mapToTime(startTime)).build();
        double calculatedPrice = discountService.calculateDiscountPrice(showing);
        double expectedPrice = 8.0;
        assertThat(calculatedPrice).isEqualTo(expectedPrice);
    }

    @Test
    void discountSeventhDay() {
        String startTime = "2016-03-07 18:30";
        Movie movie = Movie.builder().ticketPrice(10).build();
        when(validator.validate(any(Showing.class))).thenReturn(Boolean.TRUE);
        Showing showing = Showing.builder().movie(movie).daySequence(4).showStartTime(TheaterUtils.mapToTime(startTime)).build();
        double calculatedPrice = discountService.calculateDiscountPrice(showing);
        double expectedPrice = 9;
        assertThat(calculatedPrice).isEqualTo(expectedPrice);
    }

    @Test
    void discountEarlyBirdAndNotSecondShow() {
        String startTime = "2016-03-07 11:30";
        Movie movie = Movie.builder().ticketPrice(12.5).build();
        when(validator.validate(any(Showing.class))).thenReturn(Boolean.TRUE);
        Showing showing = Showing.builder().movie(movie).daySequence(2).showStartTime(TheaterUtils.mapToTime(startTime)).build();
        double calculatedPrice = discountService.calculateDiscountPrice(showing);
        double expectedPrice = 9.38;
        assertThat(calculatedPrice).isEqualTo(expectedPrice);
    }

    @Test
    void removeEarlyBirdDiscount() {
        boolean earlyBird = discountService.removeDiscountRule("EarlyBird");
        when(validator.validate(any(Showing.class))).thenReturn(Boolean.TRUE);
        assertThat(earlyBird).isTrue();
        String startTime = "2016-03-07 11:30";
        Movie movie = Movie.builder().ticketPrice(10).build();
        Showing showing = Showing.builder().movie(movie).daySequence(2).showStartTime(TheaterUtils.mapToTime(startTime)).build();
        double calculatedPrice = discountService.calculateDiscountPrice(showing);
        double expectedPrice = 8.0;
        assertThat(calculatedPrice).isEqualTo(expectedPrice);
    }

    @Test
    void removeInvalidDiscountRule() {
        boolean earlyBird = discountService.removeDiscountRule("A Ghost");
        assertThat(earlyBird).isFalse();
    }

    @Test
    void calculateDiscountPriceError() {
        MovieTheaterException thrown = Assertions.assertThrows(MovieTheaterException.class, () -> {
            discountService.calculateDiscountPrice(Showing.builder().build());
        });
        assertThat(thrown.getMessage()).isEqualTo("Invalid showing");
    }
}