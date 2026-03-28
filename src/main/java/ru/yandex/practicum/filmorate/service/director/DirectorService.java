package ru.yandex.practicum.filmorate.service.director;

import ru.yandex.practicum.filmorate.dto.director.request.DirectorRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.director.request.DirectorRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.director.response.DirectorResponseDto;

import java.util.List;


public interface DirectorService {
    DirectorResponseDto createDirector(DirectorRequestCreateDto dto);

    DirectorResponseDto updateDirector(DirectorRequestUpdateDto dto);

    DirectorResponseDto getById(Long id);

    List<DirectorResponseDto> getAllDirectors();

    void deleteById(Long id);
}
