package com.jpmc.theater.data;

import com.jpmc.theater.domain.Movie;
import com.jpmc.theater.domain.Showing;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

public class LocalMovieRepository implements MovieRepository {

    private TreeMap<Integer, Showing> schedule;

    Movie spiderMan = new Movie("Spider-Man: No Way Home", "When a teenage boy realizes life is hard", Duration.ofMinutes(90), 12.5, 1);
    Movie turningRed = new Movie("Turning Red", "A potentially good movie, but who knows?", Duration.ofMinutes(85), 11, 0);
    Movie theBatMan = new Movie("The Batman", "An adult male that wants to be a vigilante. He goes around killing bad guys", Duration.ofMinutes(95), 9, 0);

    public LocalMovieRepository(LocalDate localDate) {
        this.schedule = new TreeMap<>();
        loadInMemoryShowings(localDate);
    }

    private void loadInMemoryShowings(LocalDate localDate) {
        saveShowing(1, turningRed, LocalDateTime.of(localDate, LocalTime.of(9, 0)));
        saveShowing(2, spiderMan, LocalDateTime.of(localDate, LocalTime.of(11, 0)));
        saveShowing(3, theBatMan, LocalDateTime.of(localDate, LocalTime.of(12, 50)));
        saveShowing(4, turningRed, LocalDateTime.of(localDate, LocalTime.of(14, 30)));
        saveShowing(5, spiderMan, LocalDateTime.of(localDate, LocalTime.of(16, 10)));
        saveShowing(6, theBatMan, LocalDateTime.of(localDate, LocalTime.of(17, 50)));
        saveShowing(7, turningRed, LocalDateTime.of(localDate, LocalTime.of(19, 30)));
        saveShowing(8, spiderMan, LocalDateTime.of(localDate, LocalTime.of(21, 10)));
        saveShowing(9, theBatMan, LocalDateTime.of(localDate, LocalTime.of(23, 0)));
    }

    public boolean saveShowing(int sequence, Movie movie, LocalDateTime showingTime) {
        boolean isRequestValid = sequence <= 0 || movie == null || movie.getTitle() == null || showingTime == null;
        if (isRequestValid) {
            return Boolean.FALSE;
        }
        this.schedule.put(sequence, Showing.builder().daySequence(sequence).movie(movie).showStartTime(showingTime).build());
        return Boolean.TRUE;
    }

    public Optional<Showing> getShowing(int sequence) {
        return Optional.ofNullable(this.schedule.get(sequence));
    }

    public List<Showing> getFullSchedule() {
        return new ArrayList<>(this.schedule.values());
    }

}
