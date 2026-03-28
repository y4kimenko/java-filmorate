package ru.yandex.practicum.filmorate.web.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.DirectorFilmsSortBy;

import java.util.Locale;

@Component
public class StringToDirectorFilmsSortByConverter
        implements Converter<String, DirectorFilmsSortBy> {

    @Override
    public DirectorFilmsSortBy convert(String source) {
        return switch (source.toLowerCase(Locale.ROOT)) {
            case "year" -> DirectorFilmsSortBy.YEAR;
            case "likes" -> DirectorFilmsSortBy.LIKES;
            default -> throw new IllegalArgumentException("Unknown sort: " + source);
        };
    }
}
