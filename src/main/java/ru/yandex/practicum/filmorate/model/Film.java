package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
public class Film {
    private static final LocalDate MIN = LocalDate.of(1895, 12, 28);

    private Long id;

    @NotEmpty(message = "name не может быть пустым")
    @NotBlank(message = "name не должно состоять из пробелов")
    private String name;

    @Size(max = 201, message = "у description максимальная длина 200 символов")
    private String description;

    @NotNull(message = "releaseDate не может быть пустой")
    private LocalDate releaseDate;

    @NotNull(message = "duration не может быть пустым")
    @Min(value = 1, message = "duration должна составлять не меньше 1 минуты")
    private Integer duration;

    private Set<Long> likedUser = new HashSet<>();

    @AssertTrue(message = "releaseDate не может быть раньше чем 28.12.1895")
    public boolean isReleaseDateValid() {
        if (releaseDate == null) return true;
        return !releaseDate.isBefore(MIN);
    }


}
