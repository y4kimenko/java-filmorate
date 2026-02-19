package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.dal.genres.GenresStorage;
import ru.yandex.practicum.filmorate.dto.genre.response.GenreResponseDto;
import ru.yandex.practicum.filmorate.exception.notFound.GenreNotFoundException;
import ru.yandex.practicum.filmorate.service.genres.GenresServiceImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenresServiceImplTest {

    private final GenresStorage genresStorage = mock(GenresStorage.class);

    private final GenresServiceImpl genresService = new GenresServiceImpl(genresStorage);

    @Test
    @DisplayName("getAll – возвращает множество всех жанров из storage")
    void getAll_returnsAllGenres() {
        Map<Long, GenreResponseDto> stored = new HashMap<>();
        stored.put(1L, new GenreResponseDto(1, "Комедия"));
        stored.put(2L, new GenreResponseDto(2, "Драма"));

        when(genresStorage.getAll()).thenReturn(stored);

        Set<GenreResponseDto> result = genresService.getAll();

        assertEquals(2, result.size());
        assertTrue(result.contains(new GenreResponseDto(1, "Комедия")));
        assertTrue(result.contains(new GenreResponseDto(2, "Драма")));
        verify(genresStorage).getAll();
    }

    @Test
    @DisplayName("getAll – возвращает пустое множество если storage пустой")
    void getAll_returnsEmptySet_whenStorageEmpty() {
        when(genresStorage.getAll()).thenReturn(Map.of());

        Set<GenreResponseDto> result = genresService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(genresStorage).getAll();
    }

    @Test
    @DisplayName("getById – возвращает жанр если найден")
    void getById_returnsGenre_whenFound() {
        GenreResponseDto dto = new GenreResponseDto(1, "Комедия");
        when(genresStorage.getById(1L)).thenReturn(Optional.of(dto));

        GenreResponseDto result = genresService.getById(1L);

        assertEquals(dto, result);
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
