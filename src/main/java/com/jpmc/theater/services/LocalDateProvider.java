package com.jpmc.theater.services;

import java.time.LocalDate;

public enum LocalDateProvider {

    INSTANCE;
    private LocalDate time;

    public LocalDate getShowingDate() {
        if (time == null) {
            time = LocalDate.now();
        }
        return time;
    }

    /**
     * Method Added in case current date needs to be changed to a new one
     */
    public void updateDate() {
        time = LocalDate.now();
    }

}
