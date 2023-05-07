package com.jpmc.theater;

import com.jpmc.theater.data.MovieRepository;
import com.jpmc.theater.domain.*;
import com.jpmc.theater.services.DiscountService;
import com.jpmc.theater.services.LocalDateProvider;
import com.jpmc.theater.utils.TheaterUtils;
import com.jpmc.theater.utils.Validator;
import lombok.SneakyThrows;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Theater {

    private DiscountService discountService;
    private MovieRepository movieRepository;
    private LocalDateProvider localDateProvider;
    private Validator validator;


    public Theater(DiscountService discountService, MovieRepository movieRepository, LocalDateProvider localDateProvider, Validator validator) {
        this.discountService = discountService;
        this.movieRepository = movieRepository;
        this.localDateProvider = localDateProvider;
        this.validator = validator;
    }

    @SneakyThrows
    public Reservation reserve(Customer customer, int sequence, int howManyTickets) {
        Showing showing = movieRepository.getShowing(sequence)
                .orElseThrow(() -> new MovieTheaterException("not able to find any showing for given sequence"));
        if (!validator.validate(showing)) {
            throw new MovieTheaterException("Showing is in an invalid state showing=" + showing);
        }
        double discountAmount = discountService.calculateDiscountPrice(showing);
        return new Reservation(customer, showing, howManyTickets, discountAmount);
    }

    public void printSchedule(String content) {
        System.out.println(localDateProvider.getShowingDate());
        System.out.println("===================================================");
        System.out.println(content);
        System.out.println("===================================================");
    }

    public String textSchedule() {
        StringBuilder textBuilder = new StringBuilder();
        List<Showing> fullSchedule = movieRepository.getFullSchedule();
        if (fullSchedule == null || fullSchedule.size() == 0) {
            return "No movies currently available";
        }
        for (Showing showing : fullSchedule) {
            if (validator.validate(showing)) {
                textBuilder.append(showing.getDaySequence() + ": ");
                textBuilder.append(Optional.ofNullable(showing.getShowStartTime()).map(LocalDateTime::toString).orElse("Time not available") + " ");
                textBuilder.append(showing.getMovie().getTitle() + " - ");
                textBuilder.append(showing.getMovie().getDescription() + ". ");
                textBuilder.append(TheaterUtils.humanReadableFormat(showing.getMovie().getRunningTime()) + " $");
                textBuilder.append(discountService.calculateDiscountPrice(showing));
                textBuilder.append("\n");
            } else {
                textBuilder.append("Movie is not available\n");
            }
        }
        textBuilder.deleteCharAt(textBuilder.length() - 1);
        return textBuilder.toString();
    }


    public String jsonSchedule() {
        List<String> collect = Optional
                .ofNullable(movieRepository.getFullSchedule())
                .orElse(Collections.emptyList())
                .stream()
                .filter(validator::validate)
                .map(showing -> {
                    double discountPrice = discountService.calculateDiscountPrice(showing);
                    return showing.toJson(Optional.of(discountPrice));
                }).collect(Collectors.toList());
        String jsonResult = String.join(",", collect);
        return "[" + jsonResult + "]";
    }
}
