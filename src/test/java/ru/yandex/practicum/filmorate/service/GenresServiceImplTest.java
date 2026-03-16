package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.dal.genres.GenresStorage;
import ru.yandex.practicum.filmorate.dto.genre.response.GenreResponseDto;
import ru.yandex.practicum.filmorate.exception.notFound.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.genres.GenresServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenresServiceImplTest {

    private final GenresStorage genresStorage = mock(GenresStorage.class);

    private final GenresServiceImpl genresService = new GenresServiceImpl(genresStorage);

    @Test
    @DisplayName("getAll – возвращает множество всех жанров из storage")
    void getAll_returnsAllGenres() {
        Map<Long, Genre> stored = new HashMap<>();
        stored.put(1L, new Genre(1L, "Комедия"));
        stored.put(2L, new Genre(2L, "Драма"));

        when(genresStorage.getAll()).thenReturn(stored);

        List<GenreResponseDto> result = genresService.getAll();

        assertEquals(2, result.size());
        assertTrue(result.contains(new GenreResponseDto(1, "Комедия")));
        assertTrue(result.contains(new GenreResponseDto(2, "Драма")));
        verify(genresStorage).getAll();
    }

    @Test
    @DisplayName("getAll – возвращает пустое множество если storage пустой")
    void getAll_returnsEmptySet_whenStorageEmpty() {
        when(genresStorage.getAll()).thenReturn(Map.of());

        List<GenreResponseDto> result = genresService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(genresStorage).getAll();
    }

    @Test
    @DisplayName("getById – возвращает жанр если найден")
    void getById_returnsGenre_whenFound() {
        Genre dto = new Genre(1L, "Комедия");
        when(genresStorage.getById(1L)).thenReturn(Optional.of(dto));

        GenreResponseDto result = genresService.getById(1L);

        assertEquals(new GenreResponseDto(1L, "Комедия"), result);
        verify(genresStorage).getById(1L);
    }

    @Test
    @DisplayName("getById – кидает GenreNotFoundException если жанр не найден")
    void getById_throws_whenNotFound() {
        when(genresStorage.getById(999L)).thenReturn(Optional.empty());

        GenreNotFoundException ex = assertThrows(
                GenreNotFoundException.class,
                () -> genresService.getById(999L)
        );

        assertEquals("Genre c id=999 не найден.", ex.getMessage());
        verify(genresStorage).getById(999L);
    }
}
