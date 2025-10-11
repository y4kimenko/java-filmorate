package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.validation.annotation.Validated;


import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    private static final LocalDate MIN = LocalDate.of(1895, 12, 28);

    long id;

    @NotBlank(message = "name must not be empty")
    String name;

    @Length(max = 200, message = "description has a maximum length of 200 characters")
    String description;

    @NotNull(message = "releaseDate must not be empty")
    Instant releaseDate;

    @NotNull(message = "duration must not be empty")
    @DurationMin(minutes = 1, message = "duration must be at least 1 minutes")
    Duration duration;


    @AssertTrue(message = "releaseDate not earlier than 28.12.189")
    public boolean isReleaseDateValid() {
        return releaseDate == null || !releaseDate.isBefore(Instant.from(MIN));
    }
}
