package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.time.DurationMin;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * Film.
 */
@Data
public class Film {
    private static final LocalDate MIN = LocalDate.of(1895, 12, 28);

    // @Null(groups = OnCreate.class)
    // @NotNull(groups = OnUpdate.class)
    Long id;

    @NotEmpty(message = "name must not be empty")
    @NotBlank(message = "name must not consist of spaces")
    String name;

    @Size(max = 200, message = "description has a maximum length of 200 characters")
    String description;

    @NotNull(message = "releaseDate must not be empty")
    Instant releaseDate;

    @NotNull(message = "duration must not be empty")
    @DurationMin(minutes = 1, message = "duration must be at least 1 minutes")
    Duration duration;


    @AssertTrue(message = "releaseDate not earlier than 28.12.1895")
    public boolean isReleaseDateValid() {
        if (releaseDate == null) return true;
        var minInstant = MIN.atStartOfDay(ZoneOffset.UTC).toInstant();
        return !releaseDate.isBefore(minInstant);
    }


    public interface OnCreate {
    }

    public interface OnUpdate {
    }
}
