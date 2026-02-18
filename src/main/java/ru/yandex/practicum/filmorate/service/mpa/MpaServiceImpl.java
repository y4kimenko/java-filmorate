package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.dto.mpa.response.MpaResponseDto;
import ru.yandex.practicum.filmorate.exception.notFound.MpaNotFoundException;


import java.util.Set;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final MpaStorage mpaStorage;

    public Set<MpaResponseDto> getAll() {
        return mpaStorage.getAll();
    }

    public MpaResponseDto getById(long id) {
        return mpaStorage.getById(id).orElseThrow(() -> new MpaNotFoundException("Mpa c id=" + id + " не найден."));
    }
}
