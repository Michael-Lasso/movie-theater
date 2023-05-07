package com.jpmc.theater;

import com.jpmc.theater.data.MovieRepository;
import com.jpmc.theater.domain.*;
import com.jpmc.theater.services.DiscountService;
import com.jpmc.theater.services.LocalDateProvider;
import com.jpmc.theater.utils.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TheaterTests {

    @Mock
    private DiscountService discountService;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private Validator validator;
    @Spy
    private LocalDateProvider localDateProvider = LocalDateProvider.INSTANCE;
    private Theater theater;

    @BeforeEach
    void init() {
        theater = new Theater(discountService, movieRepository, localDateProvider, validator);
    }

    @Test
    void totalFeeForCustomer() {
        Customer john = Customer.builder().id("id-12345").name("John Doe").build();
        Showing showing = Showing.builder().daySequence(1).build();
        when(discountService.calculateDiscountPrice(showing)).thenReturn(10.0);
        when(movieRepository.getShowing(1)).thenReturn(Optional.of(showing));
        when(validator.validate(any(Showing.class))).thenReturn(Boolean.TRUE);
        Reservation reservation = theater.reserve(john, 1, 4);
        assertThat(reservation.totalFee()).isEqualTo(40);
    }

    @Test
    void printSchedule() {
        theater.printSchedule("some content");
        verify(localDateProvider, times(1)).getShowingDate();
    }

    @Test
    void reserveSuccess() {
        Customer customer = Customer.builder().id("some id").name("Neo").build();
        Showing build = Showing.builder().daySequence(2).build();
        when(movieRepository.getShowing(1)).thenReturn(Optional.of(build));
        when(discountService.calculateDiscountPrice(build)).thenReturn(8.0);
        when(validator.validate(any(Showing.class))).thenReturn(Boolean.TRUE);
        Reservation reserve = theater.reserve(customer, 1, 5);
        assertThat(reserve.getCustomer()).isEqualTo(customer);
        assertThat(reserve.getTicketCount()).isEqualTo(5);
        assertThat(reserve.getUpdatedPrice()).isEqualTo(8);
        assertThat(reserve.getShowing()).isEqualTo(build);
        assertThat(reserve.totalFee()).isEqualTo(40.0);
    }

    @Test
    void reserveNoShowingFound() {
        Customer customer = Customer.builder().build();
        verifyNoInteractions(discountService);
        MovieTheaterException thrown = Assertions.assertThrows(MovieTheaterException.class, () -> {
            theater.reserve(customer, 1, 5);
        });
        assertThat(thrown.getMessage()).isEqualTo("not able to find any showing for given sequence");
    }

    @Test
    void textSchedule() {
        Movie matrixI = Movie
                .builder()
                .ticketPrice(15.0)
                .title("The Matrix")
                .description("Best movie ever, convince me otherwise")
                .specialCode(5)
                .runningTime(Duration.ofMinutes(136))
                .build();
        Movie matrixII = Movie
                .builder()
                .ticketPrice(15.0)
                .title("The Matrix Reloaded")
                .description("Do not watch if you haven't seen part I")
                .specialCode(5)
                .runningTime(Duration.ofMinutes(136))
                .build();
        Showing showing1 = Showing.builder().daySequence(1).movie(matrixI).build();
        Showing showing2 = Showing.builder().daySequence(2).build();
        Showing showing3 = Showing.builder().daySequence(3).movie(matrixII).build();
        List<Showing> showings = Arrays.asList(
                showing1,
                showing2,
                showing3
        );

        when(movieRepository.getFullSchedule()).thenReturn(showings);
        when(discountService.calculateDiscountPrice(any())).thenReturn(10.0);
        when(validator.validate(showing1)).thenReturn(Boolean.TRUE);
        when(validator.validate(showing2)).thenReturn(Boolean.FALSE);
        when(validator.validate(showing3)).thenReturn(Boolean.TRUE);
        String jsonResponse = theater.textSchedule();
        String expected =
                "1: Time not available The Matrix - Best movie ever, convince me otherwise. (2 hours 16 minutes) $10.0\n" +
                        "Movie is not available\n" +
                        "3: Time not available The Matrix Reloaded - Do not watch if you haven't seen part I. (2 hours 16 minutes) $10.0";
        assertThat(jsonResponse).isEqualTo(expected);
    }

    @Test
    void jsonSchedule() {
        Movie matrixI = Movie
                .builder()
                .ticketPrice(15.0)
                .title("The Matrix")
                .description("Best movie ever, convince me otherwise")
                .specialCode(5)
                .runningTime(Duration.ofMinutes(136))
                .build();
        Movie matrixII = Movie
                .builder()
                .ticketPrice(15.0)
                .title("The Matrix Reloaded")
                .description("A continuation of part I and equally as good")
                .specialCode(5)
                .runningTime(Duration.ofMinutes(136))
                .build();
        List<Showing> showings = Arrays.asList(
                Showing.builder().daySequence(1).movie(matrixI).build(),
                Showing.builder().daySequence(2).build(),
                Showing.builder().daySequence(3).movie(matrixII).build()
        );

        when(movieRepository.getFullSchedule()).thenReturn(showings);
        when(discountService.calculateDiscountPrice(any())).thenReturn(13.0);
        when(validator.validate(any(Showing.class))).thenReturn(Boolean.TRUE);
        String jsonResponse = theater.jsonSchedule();
        String expected = "[\n" +
                "  {\n" +
                " \"sequenceOfTheDay\":1,\n" +
                " \"showStartTime\":\"null\",\n" +
                "    \"movie\" :{\n" +
                "      \"title\":\"The Matrix\",\n" +
                "      \"description\":\"Best movie ever, convince me otherwise\",\n" +
                "      \"runningTime\":\"(2 hours 16 minutes)\",\n" +
                "      \"ticketPrice\":13.0\n" +
                "      }\n" +
                "}\n" +
                ",\n" +
                "  {\n" +
                " \"sequenceOfTheDay\":2,\n" +
                " \"showStartTime\":\"null\",\n" +
                "    \"movie\" :{\"description\":\"This movie is not available\"}\n" +
                "}\n" +
                ",\n" +
                "  {\n" +
                " \"sequenceOfTheDay\":3,\n" +
                " \"showStartTime\":\"null\",\n" +
                "    \"movie\" :{\n" +
                "      \"title\":\"The Matrix Reloaded\",\n" +
                "      \"description\":\"A continuation of part I and equally as good\",\n" +
                "      \"runningTime\":\"(2 hours 16 minutes)\",\n" +
                "      \"ticketPrice\":13.0\n" +
                "      }\n" +
                "}\n" +
                "]";

        assertThat(jsonResponse).isEqualTo(expected);
    }

    @Test
    void emptyOrNullJsonSchedule() {
        List<Showing> showings = Arrays.asList();
        String nullResponse = theater.jsonSchedule();
        when(movieRepository.getFullSchedule()).thenReturn(showings);
        String expected = "[]";
        verifyNoInteractions(discountService);
        String emptyResponse = theater.jsonSchedule();
        assertThat(emptyResponse).isEqualTo(expected);
        assertThat(nullResponse).isEqualTo(expected);
    }

    @Test
    void emptyOrNullTextSchedule() {
        List<Showing> showings = Arrays.asList();
        String nullResponse = theater.textSchedule();
        when(movieRepository.getFullSchedule()).thenReturn(showings);
        String expected = "No movies currently available";
        verifyNoInteractions(discountService);
        String emptyResponse = theater.textSchedule();
        assertThat(emptyResponse).isEqualTo(expected);
        assertThat(nullResponse).isEqualTo(expected);
    }

    @Test
    void invalidStateReservation() {
        Optional<Showing> showing = Optional.of(Showing.builder().daySequence(1).build());
        when(movieRepository.getShowing(1)).thenReturn(showing);
        when(validator.validate(any(Showing.class))).thenReturn(Boolean.FALSE);
        MovieTheaterException thrown = Assertions.assertThrows(MovieTheaterException.class, () -> {
            theater.reserve(Customer.builder().build(), 1, 1);
        });
        assertThat(thrown.getMessage()).isEqualTo("Showing is in an invalid state showing=Showing(movie=null, daySequence=1, showStartTime=null)");
    }
}
