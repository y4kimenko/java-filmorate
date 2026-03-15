package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.dal.film.FilmStorage;
import ru.yandex.practicum.filmorate.dal.likes.LikesStorage;
import ru.yandex.practicum.filmorate.dal.user.UserStorage;
import ru.yandex.practicum.filmorate.exception.notFound.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.notFound.UserNotFoundException;
import ru.yandex.practicum.filmorate.service.likes.LikesServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikesServiceImplTest {

    private final LikesStorage likesStorage = mock(LikesStorage.class);
    private final UserStorage userStorage = mock(UserStorage.class);
    private final FilmStorage filmStorage = mock(FilmStorage.class);

    private final LikesServiceImpl likesService =
            new LikesServiceImpl(likesStorage, userStorage, filmStorage);

    @Test
    @DisplayName("addUserLike – кидает UserNotFoundException если user не найден")
    void addUserLike_throws_whenUserNotFound() {
        when(userStorage.existsById(7L)).thenReturn(false);

        UserNotFoundException ex = assertThrows(
                UserNotFoundException.class,
                () -> likesService.addUserLike(10L, 7L)
        );

        assertEquals("User c id=7 не найден.", ex.getMessage());
        verify(userStorage).existsById(7L);
        verifyNoInteractions(filmStorage);
        verifyNoInteractions(likesStorage);
    }

    @Test
    @DisplayName("addUserLike – кидает FilmNotFoundException если film не найден")
    void addUserLike_throws_whenFilmNotFound() {
        when(userStorage.existsById(7L)).thenReturn(true);
        when(filmStorage.existsById(10L)).thenReturn(false);

        FilmNotFoundException ex = assertThrows(
                FilmNotFoundException.class,
                () -> likesService.addUserLike(10L, 7L)
        );

        assertEquals("Film c id=10 не найден.", ex.getMessage());
        verify(userStorage).existsById(7L);
        verify(filmStorage).existsById(10L);
        verifyNoInteractions(likesStorage);
    }

    @Test
    @DisplayName("addUserLike – успех: вызывает likesStorage.addLikeFilmByUser(userId, filmId)")
    void addUserLike_success_callsStorage() {
        when(userStorage.existsById(7L)).thenReturn(true);
        when(filmStorage.existsById(10L)).thenReturn(true);

        likesService.addUserLike(10L, 7L);

        verify(likesStorage).addLikeFilmByUser(7L, 10L);
        verify(likesStorage, never()).removeLikeFilmByUser(anyLong(), anyLong());
    }

    @Test
    @DisplayName("removeUserLike – кидает UserNotFoundException если user не найден")
    void removeUserLike_throws_whenUserNotFound() {
        when(userStorage.existsById(7L)).thenReturn(false);

        UserNotFoundException ex = assertThrows(
                UserNotFoundException.class,
                () -> likesService.removeUserLike(10L, 7L)
        );

        assertEquals("User c id=7 не найден.", ex.getMessage());
        verify(userStorage).existsById(7L);
        verifyNoInteractions(filmStorage);
        verifyNoInteractions(likesStorage);
    }

    @Test
    @DisplayName("removeUserLike – кидает FilmNotFoundException если film не найден")
    void removeUserLike_throws_whenFilmNotFound() {
        when(userStorage.existsById(7L)).thenReturn(true);
        when(filmStorage.existsById(10L)).thenReturn(false);

        FilmNotFoundException ex = assertThrows(
                FilmNotFoundException.class,
                () -> likesService.removeUserLike(10L, 7L)
        );

        assertEquals("Film c id=10 не найден.", ex.getMessage());
        verify(userStorage).existsById(7L);
        verify(filmStorage).existsById(10L);
        verifyNoInteractions(likesStorage);
    }

    @Test
    @DisplayName("removeUserLike – успех: likesStorage.removeLikeFilmByUser возвращает true")
    void removeUserLike_success_whenLikeExists() {
        when(userStorage.existsById(7L)).thenReturn(true);
        when(filmStorage.existsById(10L)).thenReturn(true);
        when(likesStorage.removeLikeFilmByUser(7L, 10L)).thenReturn(true);

        assertDoesNotThrow(() -> likesService.removeUserLike(10L, 7L));

        verify(likesStorage).removeLikeFilmByUser(7L, 10L);
    }


}
