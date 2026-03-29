package ru.yandex.practicum.filmorate.web.controller.film;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.director.request.DirectorRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.director.request.DirectorRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.director.response.DirectorResponseDto;
import ru.yandex.practicum.filmorate.service.director.DirectorService;

import java.util.List;


@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
@Validated
public class DirectorController {

    private final DirectorService directorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DirectorResponseDto createDirector(@RequestBody
                                              @NotNull
                                              @Valid
                                              DirectorRequestCreateDto dto
    ) {
        return directorService.createDirector(dto);
    }

    @PutMapping
    public DirectorResponseDto updateDirector(@RequestBody
                                              @NotNull
                                              @Valid
                                              DirectorRequestUpdateDto dto
    ) {
        return directorService.updateDirector(dto);
    }

    @GetMapping("/{id}")
    public DirectorResponseDto getDirectorById(@PathVariable
                                               @PositiveOrZero(message = "id не может быть отрицательным")
                                               Long id
    ) {
        return directorService.getById(id);
    }

    @GetMapping
    public List<DirectorResponseDto> getAllDirectors() {
        return directorService.getAllDirectors();
    }

    @DeleteMapping("/{id}")
    public void deleteDirectorById(@PathVariable
                                   @PositiveOrZero(message = "id не может быть отрицательным")
                                   Long id) {
        directorService.deleteById(id);
    }


}
