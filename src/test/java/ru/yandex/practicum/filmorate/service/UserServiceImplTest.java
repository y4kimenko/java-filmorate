package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.dal.user.UserStorage;
import ru.yandex.practicum.filmorate.dto.user.request.UserRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.user.request.UserRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.user.response.UserResponseDto;
import ru.yandex.practicum.filmorate.exception.notFound.UserNotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.user.UserServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private final UserStorage userStorage = mock(UserStorage.class);
    private final UserServiceImpl userService = new UserServiceImpl(userStorage);

    @Test
    @DisplayName("create – успех: сохраняет пользователя и возвращает UserResponseDto")
    void create_success_returnsDto() {
        UserRequestCreateDto dto = new UserRequestCreateDto(
                "test@mail.ru", "login", "Name", LocalDate.of(2000, 1, 1)
        );

        User entity = new User();
        entity.setEmail("test@mail.ru");
        entity.setLogin("login");
        entity.setName("Name");
        entity.setBirthday(LocalDate.of(2000, 1, 1));

        User saved = new User();
        saved.setId(10L);
        saved.setEmail(entity.getEmail());
        saved.setLogin(entity.getLogin());
        saved.setName(entity.getName());
        saved.setBirthday(entity.getBirthday());

        UserResponseDto expected = new UserResponseDto(
                10L, "test@mail.ru", "login", "Name", LocalDate.of(2000, 1, 1)
        );

        when(userStorage.save(entity)).thenReturn(saved);

        try (MockedStatic<UserMapper> mocked = mockStatic(UserMapper.class)) {
            mocked.when(() -> UserMapper.toEntity(dto)).thenReturn(entity);
            mocked.when(() -> UserMapper.toResponseDto(saved)).thenReturn(expected);

            UserResponseDto actual = userService.create(dto);

            assertEquals(expected, actual);
            verify(userStorage).save(entity);
        }
    }

    @Test
    @DisplayName("create – кидает IllegalStateException если после save id <= 0")
    void create_throws_whenIdNotGenerated() {
        UserRequestCreateDto dto = new UserRequestCreateDto(
                "test@mail.ru", "login", "Name", LocalDate.of(2000, 1, 1)
        );

        User entity = new User();

        User saved = new User();
        saved.setId(0L);

        when(userStorage.save(entity)).thenReturn(saved);

        try (MockedStatic<UserMapper> mocked = mockStatic(UserMapper.class)) {
            mocked.when(() -> UserMapper.toEntity(dto)).thenReturn(entity);

            IllegalStateException ex = assertThrows(
                    IllegalStateException.class,
                    () -> userService.create(dto)
            );

            assertEquals("Не удалось получить id после сохранения пользователя", ex.getMessage());
            verify(userStorage).save(entity);
            mocked.verify(() -> UserMapper.toResponseDto(any(User.class)), never());
        }
    }

    @Test
    @DisplayName("update – успех: обновляет существующего пользователя и возвращает UserResponseDto")
    void update_success_returnsDto() {
        UserRequestUpdateDto dto = new UserRequestUpdateDto(
                10L, "new@mail.ru", "newLogin", "New Name", LocalDate.of(1999, 12, 31)
        );

        User req = new User();
        req.setId(10L);
        req.setEmail("new@mail.ru");
        req.setLogin("newLogin");
        req.setName("New Name");
        req.setBirthday(LocalDate.of(1999, 12, 31));

        User existing = new User();
        existing.setId(10L);
        existing.setEmail("old@mail.ru");
        existing.setLogin("oldLogin");
        existing.setName("Old Name");
        existing.setBirthday(LocalDate.of(2000, 1, 1));

        User updated = new User();
        updated.setId(10L);
        updated.setEmail(req.getEmail());
        updated.setLogin(req.getLogin());
        updated.setName(req.getName());
        updated.setBirthday(req.getBirthday());

        UserResponseDto expected = new UserResponseDto(
                10L, "new@mail.ru", "newLogin", "New Name", LocalDate.of(1999, 12, 31)
        );

        when(userStorage.getById(10L)).thenReturn(Optional.of(existing));
        when(userStorage.update(existing)).thenReturn(updated);

        try (MockedStatic<UserMapper> mocked = mockStatic(UserMapper.class)) {
            mocked.when(() -> UserMapper.toEntity(dto)).thenReturn(req);
            mocked.when(() -> UserMapper.toResponseDto(updated)).thenReturn(expected);

            UserResponseDto actual = userService.update(dto);

            assertEquals(expected, actual);

            assertEquals("new@mail.ru", existing.getEmail());
            assertEquals("newLogin", existing.getLogin());
            assertEquals("New Name", existing.getName());
            assertEquals(LocalDate.of(1999, 12, 31), existing.getBirthday());

            verify(userStorage).getById(10L);
            verify(userStorage).update(existing);
        }
    }

    @Test
    @DisplayName("update – кидает UserNotFoundException если пользователь не найден")
    void update_throws_whenUserNotFound() {
        UserRequestUpdateDto dto = new UserRequestUpdateDto(
                10L, "new@mail.ru", "newLogin", "New Name", LocalDate.of(1999, 12, 31)
        );

        User req = new User();
        req.setId(10L);

        when(userStorage.getById(10L)).thenReturn(Optional.empty());

        try (MockedStatic<UserMapper> mocked = mockStatic(UserMapper.class)) {
            mocked.when(() -> UserMapper.toEntity(dto)).thenReturn(req);

            UserNotFoundException ex = assertThrows(
                    UserNotFoundException.class,
                    () -> userService.update(dto)
            );

            assertEquals("User с id=10 не найден.", ex.getMessage());
            verify(userStorage).getById(10L);
            verify(userStorage, never()).update(any());
            mocked.verify(() -> UserMapper.toResponseDto(any(User.class)), never());
        }
    }

    @Test
    @DisplayName("getAll – возвращает список UserResponseDto, маппит каждого пользователя")
    void getAll_returnsMappedList() {
        User u1 = new User();
        u1.setId(1L);
        User u2 = new User();
        u2.setId(2L);

        when(userStorage.getAll()).thenReturn(List.of(u1, u2));

        UserResponseDto dto1 = new UserResponseDto(1L, "a@a.ru", "a", "A", null);
        UserResponseDto dto2 = new UserResponseDto(2L, "b@b.ru", "b", "B", null);

        try (MockedStatic<UserMapper> mocked = mockStatic(UserMapper.class)) {
            mocked.when(() -> UserMapper.toResponseDto(u1)).thenReturn(dto1);
            mocked.when(() -> UserMapper.toResponseDto(u2)).thenReturn(dto2);

            List<UserResponseDto> result = userService.getAll();

            assertEquals(List.of(dto1, dto2), result);
            verify(userStorage).getAll();
        }
    }
}
