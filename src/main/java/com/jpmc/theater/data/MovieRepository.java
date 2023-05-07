package com.jpmc.theater.data;

import com.jpmc.theater.domain.Movie;
import com.jpmc.theater.domain.Showing;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovieRepository {

    Optional<Showing> getShowing(int sequence);
    List<Showing> getFullSchedule();
    boolean saveShowing(int sequence, Movie movie, LocalDateTime showingTime);
}
