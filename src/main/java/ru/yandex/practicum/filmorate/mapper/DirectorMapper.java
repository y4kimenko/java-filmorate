package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.director.request.DirectorRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.director.request.DirectorRequestDto;
import ru.yandex.practicum.filmorate.dto.director.request.DirectorRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.director.response.DirectorResponseDto;
import ru.yandex.practicum.filmorate.model.Director;


public class DirectorMapper {

    public static DirectorResponseDto toResponseDto(Director d) {
        return new DirectorResponseDto(d.getId(), d.getName());
    }

    public static Director toEntity(DirectorRequestCreateDto dto) {
        if (dto == null) return null;

        Director d = new Director();
        d.setName(dto.name());

        return d;
    }

    public static Director toEntity(DirectorRequestUpdateDto dto) {
        if (dto == null) return null;

        Director d = new Director();
        d.setId(dto.id());
        d.setName(dto.name());

        return d;
    }

    public static Director toEntity(DirectorRequestDto dto) {
        if (dto == null) return null;

        Director d = new Director();
        d.setId(dto.id());

        return d;
    }
}
