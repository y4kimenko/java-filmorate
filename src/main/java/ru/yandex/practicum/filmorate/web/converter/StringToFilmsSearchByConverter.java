package ru.yandex.practicum.filmorate.web.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.FilmsSearchBy;

import java.util.Locale;

@Component
public class StringToFilmsSearchByConverter implements Converter<String, FilmsSearchBy> {
    @Override
    public FilmsSearchBy convert(String source) {
        return switch (source.toLowerCase(Locale.ROOT)) {
            case "director" -> FilmsSearchBy.DIRECTOR;
            case "title" -> FilmsSearchBy.FILM_NAME;
            default -> throw new IllegalArgumentException("Unknown sort: " + source);
        };
    }
}
