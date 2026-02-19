package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.dto.mpa.response.MpaResponseDto;
import ru.yandex.practicum.filmorate.exception.notFound.MpaNotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final MpaStorage mpaStorage;

    public Set<MpaResponseDto> getAll() {
        return mpaStorage.getAll().values().stream()
                .map(MpaMapper::toResponseDto)
                .collect(Collectors.toSet());
    }

    public MpaResponseDto getById(long id) {
        return MpaMapper.toResponseDto(mpaStorage.getById(id).orElseThrow(
                () -> new MpaNotFoundException("Mpa c id=" + id + " не найден.")
        ));
    }
}
