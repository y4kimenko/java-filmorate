package ru.yandex.practicum.filmorate.dal.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    User save(User user);

    User update(User user);

    List<User> getAll();

    boolean existsById(long id);

    List<User> getByIds(Set<Long> ids);

    int deleteById(long id);

    Optional<User> findById(long id);
}
