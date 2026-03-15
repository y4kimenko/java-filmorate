package ru.yandex.practicum.filmorate.service.mpa;

import ru.yandex.practicum.filmorate.dto.mpa.response.MpaResponseDto;

import java.util.List;

public interface MpaService {
    List<MpaResponseDto> getAll();

    MpaResponseDto getById(long id);
}
