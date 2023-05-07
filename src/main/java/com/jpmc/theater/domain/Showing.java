package com.jpmc.theater.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class Showing {
    private Movie movie;
    private int daySequence;
    private LocalDateTime showStartTime;

    public String toJson(Optional<Double> discountedPrice)  {
        return "\n  {\n" +
                " \"sequenceOfTheDay\":" + daySequence +",\n"+
                " \"showStartTime\":\"" + showStartTime +"\",\n"+
                "    \"movie\" :"+Optional.ofNullable(movie).map(mvie -> mvie.toJson(discountedPrice))
                .orElse("{\"description\":\"This movie is not available\"}") +"\n"+
                "}\n";
    }
}
