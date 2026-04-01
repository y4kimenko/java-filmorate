package ru.yandex.practicum.filmorate.service.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.director.directors.DirectorStorage;
import ru.yandex.practicum.filmorate.dto.director.request.DirectorRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.director.request.DirectorRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.director.response.DirectorResponseDto;
import ru.yandex.practicum.filmorate.exception.notFound.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.notFound.FilmNotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorServiceImpl implements DirectorService {
    private final DirectorStorage directorStorage;

    @Override
    public DirectorResponseDto createDirector(DirectorRequestCreateDto dto) {
        Director d = directorStorage.save(DirectorMapper.toEntity(dto));

        log.info("createDirector() – id={}, name={}", d.getId(), d.getName());

        return DirectorMapper.toResponseDto(d);
    }

    @Override
    public DirectorResponseDto updateDirector(DirectorRequestUpdateDto dto) {
        Director req = DirectorMapper.toEntity(dto);
        Long dirId = req.getId();
        log.info("updateDirector() - dto={}", dto);
        Director existing = directorStorage.findById(dirId).orElseThrow(
                () -> new DirectorNotFoundException("Director c id=" + dirId + " не найден.")
        );

        existing.setName(req.getName());

        directorStorage.update(existing);

        log.info("updateDirector() – id={}, name={}",
                existing.getId(), existing.getName());

        return DirectorMapper.toResponseDto(existing);
    }

    @Override
    public DirectorResponseDto getById(Long id) {
        log.info("getById() id={}", id);
        return DirectorMapper.toResponseDto(directorStorage.findById(id).orElseThrow(
                () -> new DirectorNotFoundException("Director c id=" + id + " не найден.")
        ));
    }

    @Override
    public List<DirectorResponseDto> getAllDirectors() {
        log.info("getAllDirectors()");
        return directorStorage.getAll().values().stream()
                .sorted(Comparator.comparingLong(Director::getId))
                .map(DirectorMapper::toResponseDto)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        if (directorStorage.deleteById(id) == 0) {
            throw new FilmNotFoundException("Director с id = " + id + " не найден");
        }
        log.info("deleteByIdDirector() – id={}", id);
    }


}
