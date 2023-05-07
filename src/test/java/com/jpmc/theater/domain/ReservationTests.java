package com.jpmc.theater.domain;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
public class ReservationTests {

    @Test
    void totalFee() {
        var customer = new Customer("unused-id", "Michael Lasso");
        var showing = new Showing(
                new Movie("Spider-Man: No Way Home", "description", Duration.ofMinutes(90), 12.5, 1),
                1,
                LocalDateTime.now()
        );
        Reservation reservation = new Reservation(customer, showing, 3, 10.5);
        assertThat(reservation.totalFee()).isEqualTo(31.5);
        assertThat(reservation.getUpdatedPrice()).isEqualTo(10.5);
        assertThat(reservation.getShowing()).isEqualTo(showing);
        assertThat(reservation.getCustomer()).isEqualTo(customer);
    }
}
