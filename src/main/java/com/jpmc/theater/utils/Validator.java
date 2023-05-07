package com.jpmc.theater.utils;

import com.jpmc.theater.domain.Movie;
import com.jpmc.theater.domain.Showing;

import java.util.Optional;

public class Validator {
    public boolean validate(Showing showing) {
        return Optional.ofNullable(showing)
                .filter(show -> show.getDaySequence() > 0)
                .filter(show -> show.getShowStartTime() != null)
                .map(Showing::getMovie)
                .filter(movie -> validate(movie))
                .isPresent();
    }

    public boolean validate(Movie movieRequest) {
        return Optional.ofNullable(movieRequest)
                .filter(movie -> movie.getTitle() != null)
                .filter(movie -> movie.getTicketPrice() > 0)
                .filter(movie -> movie.getRunningTime() != null)
                .isPresent();
    }
}
