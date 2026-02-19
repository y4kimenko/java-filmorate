package ru.yandex.practicum.filmorate.dal.friends;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.friend.FriendShipsDto;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@Repository
@Slf4j
@RequiredArgsConstructor
public class FriendsDbStorage implements FriendsStorage {

    private static final String ADD_FRIEND = """
            INSERT INTO friendship (requester_id, addressee_id)
            VALUES (:requester_id, :addressee_id);""";
    private static final String GET_FRIENDS = """
            SELECT requester_id, addressee_id, addressee_deleted, status
            FROM friendship
            WHERE requester_id = :id
               OR (addressee_id = :id AND addressee_deleted = false AND status = true);""";
    private static final String GET_FRIENDSHIP = """
            SELECT requester_id, addressee_id, addressee_deleted, status
            FROM friendship
            WHERE requester_id = :user_id AND addressee_id = :friend_id
                OR (addressee_id = :user_id AND addressee_deleted = false AND status = true AND requester_id = :friend_id);""";
    private static final String REMOVE_FRIENDSHIP = """
            DELETE FROM friendship
            WHERE requester_id = :requester and addressee_id = :addressee;""";
    private static final String MERGE_FRIENSHIP = """
            MERGE INTO friendship (requester_id, addressee_id, addressee_deleted, status)
            KEY (requester_id, addressee_id)
            VALUES (:requester_id, :addressee_id, :addressee_deleted, :status);""";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(long requesterId, long addresseeId) {
        jdbcTemplate.update(ADD_FRIEND, new MapSqlParameterSource()
                .addValue("requester_id", requesterId)
                .addValue("addressee_id", addresseeId)
        );
    }

    @Override
    public Optional<FriendShipsDto> getFriendship(long userId, long friendId) {
        return jdbcTemplate.query(GET_FRIENDSHIP,
                new MapSqlParameterSource()
                        .addValue("user_id", userId)
                        .addValue("friend_id", friendId),
                new DataClassRowMapper<>(FriendShipsDto.class)
        ).stream().findFirst();
    }

    @Override
    public void updateFriendships(FriendShipsDto dto) {
        jdbcTemplate.update(MERGE_FRIENSHIP,
                new MapSqlParameterSource()
                        .addValue("requester_id", dto.requesterId())
                        .addValue("addressee_id", dto.addresseeId())
                        .addValue("addressee_deleted", dto.addresseeDeleted())
                        .addValue("status", dto.status())
        );
    }

    @Override
    public void deleteFriendships(long requester, long addressee) {
        jdbcTemplate.update(REMOVE_FRIENDSHIP,
                new MapSqlParameterSource()
                        .addValue("requester", requester)
                        .addValue("addressee", addressee)
        );
    }

    @Override
    public Set<Long> getFriends(long userId) {
        return new HashSet<>(jdbcTemplate.query(GET_FRIENDS,
                new MapSqlParameterSource("id", userId),
                (rs, rowNum) -> {
                    long req = rs.getLong("requester_id");
                    long add = rs.getLong("addressee_id");

                    if (userId == req) return add;
                    else return req;
                }));
    }
}
