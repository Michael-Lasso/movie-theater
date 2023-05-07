package com.jpmc.theater.data;

import com.jpmc.theater.domain.Movie;
import com.jpmc.theater.domain.Showing;
import com.jpmc.theater.utils.TheaterUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Optional;
import java.util.function.Function;

public class DiscountRules {
    public static final String SPECIAL_MOVIE = "SpecialMovie";
    public static final String FIRST_SHOW = "FirstShow";
    public static final String SECOND_SHOW = "SecondShow";
    public static final String EARLY_BIRD = "EarlyBird";
    public static final String SEVENTH_DAY = "SeventhDay";

    public static final Function<Showing, Double> specialMovieRule = showing ->
            Optional.ofNullable(showing)
                    .map(Showing::getMovie)
                    .map(Movie::getSpecialCode)
                    .filter(code -> code == TheaterUtils.MOVIE_CODE_SPECIAL)
                    .isPresent() ?
                    showing.getMovie().getTicketPrice() - (showing.getMovie().getTicketPrice() * 0.2) :
                    showing.getMovie().getTicketPrice();

    public static final Function<Showing, Double> firstShowingRule = showing ->
            Optional.ofNullable(showing)
                    .map(Showing::getDaySequence)
                    .filter(daySequence -> daySequence == 1)
                    .isPresent() ?
                    showing.getMovie().getTicketPrice() - 3 : showing.getMovie().getTicketPrice();

    public static final Function<Showing, Double> secondShowingRule = showing ->
            Optional.ofNullable(showing)
                    .map(Showing::getDaySequence)
                    .filter(daySequence -> daySequence == 2)
                    .isPresent() ?
                    showing.getMovie().getTicketPrice() - 2 : showing.getMovie().getTicketPrice();

    public static final Function<Showing, Double> earlyBirdRule = showing ->
        Optional.ofNullable(showing)
                .map(Showing::getShowStartTime)
                .filter(time -> time.isAfter(LocalDateTime.of(time.toLocalDate(), LocalTime.of(10, 59, 59))))
                .filter(time -> time.isBefore(LocalDateTime.of(time.toLocalDate(), LocalTime.of(16, 1, 0))))
                .isPresent() ?
                showing.getMovie().getTicketPrice() - (showing.getMovie().getTicketPrice() * 0.25) : showing.getMovie().getTicketPrice();


    public static final Function<Showing, Double> seventhDayRule = showing ->
            Optional.ofNullable(showing)
                    .map(Showing::getShowStartTime)
                    .map(LocalDateTime::getDayOfMonth)
                    .filter(dayOfMonth -> dayOfMonth == 7)
                    .isPresent() ?
                    showing.getMovie().getTicketPrice() - 1 : showing.getMovie().getTicketPrice();
}
