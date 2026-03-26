package ru.yandex.practicum.filmorate.dal.likes;


import java.util.List;

import java.util.Set;

public interface LikesStorage {

    boolean addLikeFilmByUser(long userId, long filmsId);

    boolean removeLikeFilmByUser(long userId, long filmId);

    List<Long> getFilmsSortedByLikes(Set<Long> filmsIds);

}
