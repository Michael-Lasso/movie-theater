package com.jpmc.theater.services;

import com.jpmc.theater.domain.MovieTheaterException;
import com.jpmc.theater.domain.Showing;
import com.jpmc.theater.utils.Validator;
import lombok.SneakyThrows;
import org.apache.commons.math3.util.Precision;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import static com.jpmc.theater.data.DiscountRules.*;

public class DiscountService {

    private Map<String, Function<Showing, Double>> discountRuleMap;
    private Validator validator;

    public DiscountService(Validator validator) {
        this.validator = validator;
        this.discountRuleMap = new TreeMap<>();
    }

    public void loadDiscountRules() {
        addDiscountRule(SPECIAL_MOVIE, specialMovieRule);
        addDiscountRule(FIRST_SHOW, firstShowingRule);
        addDiscountRule(SECOND_SHOW, secondShowingRule);
        addDiscountRule(EARLY_BIRD, earlyBirdRule);
        addDiscountRule(SEVENTH_DAY, seventhDayRule);
    }

    @SneakyThrows
    public double calculateDiscountPrice(Showing showing) {
        if (!validator.validate(showing)) {
            throw new MovieTheaterException("Invalid showing");
        }
        double result = Double.MAX_VALUE;
        for (String discountRule : discountRuleMap.keySet()) {
            Function<Showing, Double> showingDoubleFunction = discountRuleMap.get(discountRule);
            Double apply = showingDoubleFunction.apply(showing);
            result = Math.min(result, apply);
        }
        return Precision.round(result, 2);
    }

    public boolean addDiscountRule(String ruleName, Function<Showing, Double> funct) {
        boolean isRequestValid = ruleName == null || ruleName.length() == 0 || funct == null;
        if (isRequestValid) {
            return Boolean.FALSE;
        } else if (discountRuleMap.containsKey(ruleName)) {
            return Boolean.FALSE;
        } else {
            discountRuleMap.put(ruleName, funct);
            return Boolean.TRUE;
        }
    }

    public boolean removeDiscountRule(String ruleName) {
        boolean isRequestValid = ruleName == null || ruleName.length() == 0 || !discountRuleMap.containsKey(ruleName);
        if (isRequestValid) {
            return Boolean.FALSE;
        }
        discountRuleMap.remove(ruleName);
        return Boolean.TRUE;
    }

    public int discountRuleSize() {
        return discountRuleMap.size();
    }
}
