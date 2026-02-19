package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.mpa.response.MpaResponseDto;
import ru.yandex.practicum.filmorate.model.Mpa;

public class MpaMapper {
    public static MpaResponseDto toResponseDto(Mpa mpa) {
        return new MpaResponseDto(mpa.id(), mpa.name());
    }
}
