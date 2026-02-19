package ru.yandex.practicum.filmorate.dal.mpa;


import ru.yandex.practicum.filmorate.dto.mpa.response.MpaResponseDto;

import java.util.Optional;
import java.util.Set;


public interface MpaStorage {
    Set<MpaResponseDto> getAll();

    Optional<MpaResponseDto> getById(Long id);
}
