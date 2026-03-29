package ru.yandex.practicum.filmorate.model;


import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


@Data
public class Film {

    private Long id;

    private String name;

    private Map<Long, Genre> genres = new HashMap<>();

    private Mpa mpa = null;

    private Map<Long, Director> directors = new HashMap<>();

    private String description;

    private LocalDate releaseDate;

    private Integer duration;

}
