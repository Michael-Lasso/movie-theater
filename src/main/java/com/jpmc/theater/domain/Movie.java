package com.jpmc.theater.domain;

import com.jpmc.theater.utils.TheaterUtils;
import lombok.*;

import java.time.Duration;
import java.util.Optional;

@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class Movie {

    private String title;
    private String description;
    private Duration runningTime;
    private double ticketPrice;
    private int specialCode;

    public String toJson(Optional<Double> discountedPrice) {
        return "{\n" +
                "      \"title\":\"" + title + "\",\n" +
                "      \"description\":\"" + description + "\",\n" +
                "      \"runningTime\":" + "\""+TheaterUtils.humanReadableFormat(runningTime) +"\",\n"+
                "      \"ticketPrice\":" + discountedPrice.orElse(ticketPrice) +"\n"+
                "      }";
    }
}