package com.jpmc.theater.data;

import com.jpmc.theater.domain.Movie;
import com.jpmc.theater.domain.Showing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class LocalMovieRepositoryTest {

    private MovieRepository movieRepository;
    private LocalDate localDate = LocalDate.of(2023, 05, 06);

    @BeforeEach
    public void init() {
        movieRepository = new LocalMovieRepository(localDate);
    }

    @Test
    void getShowing() {
        Optional<Showing> showing = movieRepository.getShowing(1);
        assertThat(showing).isNotNull();
        assertThat(showing).isNotNull();
    }

    @Test
    void getShowingNotPresent() {
        Optional<Showing> showing1 = movieRepository.getShowing(10);
        assertThat(showing1).isEmpty();

        Optional<Showing> showing2 = movieRepository.getShowing(-1);
        assertThat(showing2).isEmpty();
    }

    @Test
    void getFullSchedule() {
        List<Showing> fullSchedule = movieRepository.getFullSchedule();
        assertThat(fullSchedule.size()).isEqualTo(9);
        assertThat(fullSchedule.stream().map(Showing::getShowStartTime)
                .map(LocalDateTime::toLocalDate).collect(Collectors.toSet()))
                .containsExactly(localDate);
    }

    @Test
    void saveShow() {
        Movie matrixI = Movie
                .builder()
                .ticketPrice(15.0)
                .title("The Matrix")
                .description("Best movie ever, convince me otherwise")
                .specialCode(5)
                .runningTime(Duration.ofMinutes(136))
                .build();
        Showing showing = Showing
                .builder()
                .movie(matrixI)
                .daySequence(10)
                .showStartTime(LocalDateTime.of(localDate, LocalTime.of(23, 0)))
                .build();

        boolean invalidMovie = movieRepository.saveShowing(10, Movie.builder().build(), LocalDateTime.of(localDate, LocalTime.of(23, 0)));
        boolean invalidTime = movieRepository.saveShowing(10, Movie.builder().build(), null);
        boolean invalidSequence = movieRepository.saveShowing(-1, Movie.builder().build(), LocalDateTime.of(localDate, LocalTime.of(23, 0)));
        boolean validShowing = movieRepository.saveShowing(10, matrixI, LocalDateTime.of(localDate, LocalTime.of(23, 0)));

        assertThat(invalidMovie).isFalse();
        assertThat(invalidTime).isFalse();
        assertThat(invalidSequence).isFalse();
        assertThat(validShowing).isTrue();
        assertThat(movieRepository.getFullSchedule().size()).isEqualTo(10);
        assertThat(movieRepository.getShowing(10).isPresent()).isEqualTo(true);
        assertThat(movieRepository.getShowing(10).get()).isEqualTo(showing);
    }
}