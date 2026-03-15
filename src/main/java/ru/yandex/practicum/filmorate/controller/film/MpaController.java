package ru.yandex.practicum.filmorate.controller.film;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dal.likes.LikesStorage;
import ru.yandex.practicum.filmorate.dto.mpa.response.MpaResponseDto;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Validated
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public List<MpaResponseDto> getMpa() {
        return mpaService.getAll();
    }

    @GetMapping("/{id}")
    public MpaResponseDto getMpaById(@PathVariable
                                     @NotNull(message = "id mpa обязателен")
                                     @PositiveOrZero(message = "id mpa не может быть отрицательным")
                                     long id
    ) {
        return mpaService.getById(id);
    }
}
