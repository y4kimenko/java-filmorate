package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.dal.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.dto.mpa.response.MpaResponseDto;
import ru.yandex.practicum.filmorate.exception.notFound.MpaNotFoundException;
import ru.yandex.practicum.filmorate.service.mpa.MpaServiceImpl;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MpaServiceImplTest {

    private final MpaStorage mpaStorage = mock(MpaStorage.class);

    private final MpaServiceImpl mpaService = new MpaServiceImpl(mpaStorage);

    @Test
    @DisplayName("getAll – возвращает набор рейтингов из storage")
    void getAll_returnsAll() {
        Set<MpaResponseDto> stored = Set.of(
                new MpaResponseDto(1, "G"),
                new MpaResponseDto(2, "PG")
        );

        when(mpaStorage.getAll()).thenReturn(stored);

        Set<MpaResponseDto> result = mpaService.getAll();

        assertSame(stored, result);
        verify(mpaStorage).getAll();
        verifyNoMoreInteractions(mpaStorage);
    }

    @Test
    @DisplayName("getById – возвращает рейтинг если найден")
    void getById_returns_whenFound() {
        MpaResponseDto dto = new MpaResponseDto(1, "G");
        when(mpaStorage.getById(1L)).thenReturn(Optional.of(dto));

        MpaResponseDto result = mpaService.getById(1L);

        assertEquals(dto, result);
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
