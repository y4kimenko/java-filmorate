package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.dal.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.dto.mpa.response.MpaResponseDto;
import ru.yandex.practicum.filmorate.exception.notFound.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.mpa.MpaServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MpaServiceImplTest {

    private final MpaStorage mpaStorage = mock(MpaStorage.class);

    private final MpaServiceImpl mpaService = new MpaServiceImpl(mpaStorage);

    @Test
    @DisplayName("getAll – возвращает набор рейтингов из storage")
    void getAll_returnsAll() {
        Map<Long, Mpa> stored = Map.of(
                1L, new Mpa(1L, "G"),
                2L, new Mpa(2L, "PG")
        );

        when(mpaStorage.getAll()).thenReturn(stored);

        List<MpaResponseDto> result = mpaService.getAll();


        assertTrue(result.contains(new MpaResponseDto(1L, "G")));
        assertTrue(result.contains(new MpaResponseDto(2L, "PG")));

        verify(mpaStorage).getAll();
        verifyNoMoreInteractions(mpaStorage);
    }

    @Test
    @DisplayName("getById – возвращает рейтинг если найден")
    void getById_returns_whenFound() {
        Mpa dto = new Mpa(1L, "G");
        when(mpaStorage.getById(1L)).thenReturn(Optional.of(dto));

        MpaResponseDto result = mpaService.getById(1L);

        assertEquals(new MpaResponseDto(1L, "G"), result);
        verify(mpaStorage).getById(1L);
        verifyNoMoreInteractions(mpaStorage);
    }

    @Test
    @DisplayName("getById – кидает MpaNotFoundException если не найден")
    void getById_throws_whenNotFound() {
        when(mpaStorage.getById(999L)).thenReturn(Optional.empty());

        MpaNotFoundException ex = assertThrows(
                MpaNotFoundException.class,
                () -> mpaService.getById(999L)
        );

        assertEquals("Mpa c id=999 не найден.", ex.getMessage());
        verify(mpaStorage).getById(999L);
        verifyNoMoreInteractions(mpaStorage);
    }
}
