package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dal.user.UserStorage;
import ru.yandex.practicum.filmorate.dto.user.request.UserRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.user.request.UserRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.user.response.UserResponseDto;
import ru.yandex.practicum.filmorate.exception.notFound.UserNotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;


@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto create(UserRequestCreateDto dto) {
        User user = UserMapper.toEntity(dto);

        User saved = userStorage.save(user);

        long userId = saved.getId();
        if (userId <= 0) {
            throw new IllegalStateException("Не удалось получить id после сохранения пользователя");
        }


        log.info("createUser() – created id={}", userId);

        return UserMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto update(UserRequestUpdateDto dto) {
        User req = UserMapper.toEntity(dto);

        Long userId = req.getId();

        User existing = userStorage.getById(userId).orElseThrow(
                () -> new UserNotFoundException("User с id=" + userId + " не найден.")
        );

        existing.setEmail(req.getEmail());
        existing.setLogin(req.getLogin());
        existing.setName(req.getName());
        existing.setBirthday(req.getBirthday());

        User res = userStorage.update(existing);

        return UserMapper.toResponseDto(res);
    }

    @Override
    public List<UserResponseDto> getAll() {
        return userStorage.getAll().stream()
                .map(UserMapper::toResponseDto)
                .toList();
    }
}
