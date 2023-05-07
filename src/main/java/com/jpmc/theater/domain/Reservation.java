package com.jpmc.theater.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Reservation {
    private Customer customer;
    private Showing showing;
    private int ticketCount;
    private double updatedPrice;

    public double totalFee() {
        return ticketCount * updatedPrice;
    }
}