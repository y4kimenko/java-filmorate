package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.dto.mpa.response.MpaResponseDto;
import ru.yandex.practicum.filmorate.exception.notFound.MpaNotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final MpaStorage mpaStorage;

    public Set<MpaResponseDto> getAll() {
        Set<MpaResponseDto> map = mpaStorage.getAll().values().stream()
                .map(MpaMapper::toResponseDto)
                .sorted(Comparator.comparing(MpaResponseDto::id))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return map;
    }

    public MpaResponseDto getById(long id) {
        return MpaMapper.toResponseDto(mpaStorage.getById(id).orElseThrow(
                () -> new MpaNotFoundException("Mpa c id=" + id + " не найден.")
        ));
    }
}
