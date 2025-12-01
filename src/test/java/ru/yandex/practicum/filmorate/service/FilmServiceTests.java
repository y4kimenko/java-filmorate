package ru.yandex.practicum.filmorate.service;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilmServiceTests {
    @Mock
    FilmStorage filmStorage;

    @Mock
    UserStorage userStorage;

    @InjectMocks
    FilmService filmService;


    @Test
    void createFilmDelegatesToStorage() {
        Film film = new Film();
        when(filmStorage.createFilm(film)).thenReturn(film);

        Film created = filmService.createFilm(film);

        assertThat(created).isSameAs(film);
        verify(filmStorage).createFilm(film);
    }


    @Test
    void updateFilmRequiresExistingFilm() {
        Film film = new Film();
        film.setId(5L);
        when(filmStorage.getFilmById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> filmService.updateFilm(film))
                .isInstanceOf(FilmNotFoundException.class);

        verify(filmStorage, never()).updateFilm(any());
    }

    @Test
    void updateFilmDelegatesAfterExistenceCheck() {
        Film film = new Film();
        film.setId(8L);
        when(filmStorage.getFilmById(8L)).thenReturn(Optional.of(film));
        when(filmStorage.updateFilm(film)).thenReturn(film);

        Film updated = filmService.updateFilm(film);

        assertThat(updated).isSameAs(film);
        verify(filmStorage).updateFilm(film);
    }

    @Test
    void getAllFilmsDelegatesToStorage() {
        Film film = new Film();
        film.setId(1L);

        when(filmStorage.getAllFilms()).thenReturn(List.of(film));

        Collection<Film> list = filmStorage.getAllFilms();

        Assertions.assertThat(list).containsExactly(film);
        verify(filmStorage).getAllFilms();
    }

    @Test
    void addUserLikeAddsEntryWhenUserAndFilmExist() {
        Film film = new Film();
        film.setId(1L);
        film.setName("Test");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(10);

        when(userStorage.getUserById(10L)).thenReturn(Optional.of(new User()));
        when(filmStorage.getFilmById(3L)).thenReturn(Optional.of(film));

        filmService.addUserLike(3L, 10L);

        verify(userStorage).getUserById(10L);
        verify(filmStorage).getFilmById(3L);
    }

    @Test
    void addUserLikeFailsWhenUserMissingBeforeCheckingFilm() {
        when(userStorage.getUserById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> filmService.addUserLike(1L, 7L))
                .isInstanceOf(UserNotFoundException.class);

        verifyNoInteractions(filmStorage);
    }

    @Test
    void addUserLikeFailsWhenFilmMissing() {
        when(userStorage.getUserById(2L)).thenReturn(Optional.of(new User()));
        when(filmStorage.getFilmById(4L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> filmService.addUserLike(4L, 2L))
                .isInstanceOf(FilmNotFoundException.class);
    }

    @Test
    void removeUserLikeAddsEntryWhenUserAndFilmExist() {
        Film film = new Film();
        film.setId(1L);
        film.setName("Test");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(10);

        when(userStorage.getUserById(10L)).thenReturn(Optional.of(new User()));
        when(filmStorage.getFilmById(3L)).thenReturn(Optional.of(film));

        filmService.removeUserLike(3L, 10L);

        verify(userStorage).getUserById(10L);
        verify(filmStorage).getFilmById(3L);
    }


    @Test
    void removeUserLikeFailsWhenUserMissingBeforeCheckingFilm() {
        when(userStorage.getUserById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> filmService.removeUserLike(1L, 7L))
                .isInstanceOf(UserNotFoundException.class);

        verifyNoInteractions(filmStorage);
    }

    @Test
    void removeUserLikeFailsWhenFilmMissing() {
        when(userStorage.getUserById(2L)).thenReturn(Optional.of(new User()));
        when(filmStorage.getFilmById(4L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> filmService.removeUserLike(4L, 2L))
                .isInstanceOf(FilmNotFoundException.class);
    }

    @Test
    void getPopularFilmsReturnsSortedAndLimitedList() {
        Film filmWithTwoLikes = new Film();
        filmWithTwoLikes.getLikedUser().addAll(List.of(1L, 2L));

        Film filmWithOneLike = new Film();
        filmWithOneLike.getLikedUser().add(3L);

        Film filmWithoutLikes = new Film();

        when(filmStorage.getAllFilms()).thenReturn(List.of(filmWithOneLike, filmWithTwoLikes, filmWithoutLikes));

        List<Film> result = filmService.getPopularFilms(2);

        assertThat(result).containsExactly(filmWithTwoLikes, filmWithOneLike);
        verify(filmStorage).getAllFilms();
    }
}
