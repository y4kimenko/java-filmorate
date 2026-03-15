package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.dto.mpa.response.MpaResponseDto;
import ru.yandex.practicum.filmorate.exception.notFound.MpaNotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final MpaStorage mpaStorage;

    public List<MpaResponseDto> getAll() {
        return mpaStorage.getAll().values().stream()
                .sorted(Comparator.comparingLong(Mpa::id))
                .map(MpaMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public MpaResponseDto getById(long id) {
        return MpaMapper.toResponseDto(mpaStorage.getById(id).orElseThrow(
                () -> new MpaNotFoundException("Mpa c id=" + id + " не найден.")
        ));
    }
}
