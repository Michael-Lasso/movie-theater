package com.jpmc.theater.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class TheaterUtils {

    public static int MOVIE_CODE_SPECIAL = 1;
    private static Function<Long, String> handlePlural = lng -> lng == 1 ? "" : "s";

    public static LocalDateTime mapToTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(time, formatter);
        return dateTime;
    }

    public static String humanReadableFormat(Duration duration) {
        if (duration == null || duration.isZero() || duration.isNegative()) {
            return "not available";
        }
        String response;
        long hour = duration.toHours();
        long remainingMin = duration.toMinutes() - TimeUnit.HOURS.toMinutes(duration.toHours());
        if (hour > 0) {
            response = String.format("(%s hour%s %s minute%s)", hour, handlePlural.apply(hour), remainingMin, handlePlural.apply(remainingMin));
        } else {
            response = String.format("(%s minute%s)", remainingMin, handlePlural.apply(remainingMin));
        }
        return response;
    }

}

