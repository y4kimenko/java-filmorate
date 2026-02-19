package ru.yandex.practicum.filmorate.dal.likes;


public interface LikesStorage {


    boolean addLikeFilmByUser(long userId, long filmsId);

    boolean removeLikeFilmByUser(long userId, long filmId);


}
