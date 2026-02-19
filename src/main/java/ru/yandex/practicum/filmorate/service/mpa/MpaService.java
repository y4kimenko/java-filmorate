package ru.yandex.practicum.filmorate.service.mpa;

import ru.yandex.practicum.filmorate.dto.mpa.response.MpaResponseDto;

import java.util.Set;

public interface MpaService {
    Set<MpaResponseDto> getAll();

    MpaResponseDto getById(long id);
}
