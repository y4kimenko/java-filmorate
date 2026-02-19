package ru.yandex.practicum.filmorate.model.film;


import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
public class Film {

    private Long id;

    private String name;

    private Set<Long> genres = new HashSet<>();

    private Long mpa = null;

    private String description;

    private LocalDate releaseDate;

    private Integer duration;

}
