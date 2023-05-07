package com.jpmc.theater;

import com.jpmc.theater.data.LocalMovieRepository;
import com.jpmc.theater.data.MovieRepository;
import com.jpmc.theater.services.DiscountService;
import com.jpmc.theater.services.LocalDateProvider;
import com.jpmc.theater.utils.Validator;

public class ApplicationBootstrap {
    public static void main(String[] args) {
        //Bean instantiation and postconstruct
        LocalDateProvider instance = LocalDateProvider.INSTANCE;
        Validator validator = new Validator();
        DiscountService discountService = new DiscountService(validator);
        MovieRepository movieRepository = new LocalMovieRepository(instance.getShowingDate());
        discountService.loadDiscountRules();

        Theater theater = new Theater(discountService, movieRepository, instance, validator);

        theater.printSchedule(theater.textSchedule());
        theater.printSchedule(theater.jsonSchedule());
    }
}
