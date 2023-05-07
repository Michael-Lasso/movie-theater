package com.jpmc.theater.services;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalDateProviderTest {

    LocalDateProvider instance = LocalDateProvider.INSTANCE;
    @SneakyThrows
    @Test
    void showingDateShouldBeSame() {
        LocalDate before = instance.getShowingDate();
        TimeUnit.MILLISECONDS.sleep(2000);
        LocalDate after = instance.getShowingDate();
        assertThat(before).isEqualTo(after);
    }

    @Test
    void getNewDate() {
        instance.updateDate();
        assertThat(instance.getShowingDate()).isNotNull();
    }
}
