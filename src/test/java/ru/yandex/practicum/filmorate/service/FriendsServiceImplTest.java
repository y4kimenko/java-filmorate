package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.dal.events.EventStorage;
import ru.yandex.practicum.filmorate.dal.friends.FriendsStorage;
import ru.yandex.practicum.filmorate.dal.user.UserStorage;
import ru.yandex.practicum.filmorate.dto.friend.FriendShipsDto;
import ru.yandex.practicum.filmorate.dto.user.response.UserResponseDto;
import ru.yandex.practicum.filmorate.exception.notFound.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.friends.FriendsServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FriendsServiceImplTest {

    private final UserStorage userStorage = mock(UserStorage.class);
    private final FriendsStorage friendsStorage = mock(FriendsStorage.class);
    private final EventStorage eventStorage = mock(EventStorage.class);

    private FriendsServiceImpl friendsService;

    @BeforeEach
    void setUp() {
        friendsService = new FriendsServiceImpl(userStorage, friendsStorage, eventStorage);
    }

    @Test
    @DisplayName("addFriend – успех: оба пользователя существуют, вызывается friendsStorage.addFriend")
    void addFriend_success() {
        long requester = 1L;
        long addressee = 2L;

        when(userStorage.existsById(requester)).thenReturn(true);
        when(userStorage.existsById(addressee)).thenReturn(true);

        friendsService.addFriend(requester, addressee);

        verify(friendsStorage).addFriend(requester, addressee);
    }

    @Test
    @DisplayName("addFriend – ошибка: requester не существует")
    void addFriend_throws_whenRequesterNotFound() {
        long requester = 1L;
        long addressee = 2L;

        when(userStorage.existsById(requester)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> friendsService.addFriend(requester, addressee));

        verify(userStorage, never()).existsById(addressee);
        verifyNoInteractions(friendsStorage);
    }

    @Test
    @DisplayName("addFriend – ошибка: addressee не существует")
    void addFriend_throws_whenAddresseeNotFound() {
        long requester = 1L;
        long addressee = 2L;

        when(userStorage.existsById(requester)).thenReturn(true);
        when(userStorage.existsById(addressee)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> friendsService.addFriend(requester, addressee));

        verify(friendsStorage, never()).addFriend(anyLong(), anyLong());
    }

    @Test
    @DisplayName("removeFriend – ошибка: userId не существует")
    void removeFriend_throws_whenUserNotFound() {
        when(userStorage.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> friendsService.removeFriend(1L, 2L));

        verify(userStorage, never()).existsById(2L);
        verifyNoInteractions(friendsStorage);
    }

    @Test
    @DisplayName("removeFriend – ошибка: friendId не существует")
    void removeFriend_throws_whenFriendNotFound() {
        when(userStorage.existsById(1L)).thenReturn(true);
        when(userStorage.existsById(2L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> friendsService.removeFriend(1L, 2L));

        verifyNoInteractions(friendsStorage);
    }

    @Test
    @DisplayName("removeFriend – дружбы нет: friendsStorage.getFriendship пустой, ничего не делаем")
    void removeFriend_noFriendship_doesNothing() {
        long userId = 1L;
        long friendId = 2L;

        when(userStorage.existsById(userId)).thenReturn(true);
        when(userStorage.existsById(friendId)).thenReturn(true);
        when(friendsStorage.findFriendship(userId, friendId)).thenReturn(Optional.empty());

        friendsService.removeFriend(userId, friendId);

        verify(friendsStorage).findFriendship(userId, friendId);
        verify(friendsStorage, never()).deleteFriendships(anyLong(), anyLong());
        verify(friendsStorage, never()).updateFriendships(any());
    }

    @Test
    @DisplayName("removeFriend – userId является requester: вызывается deleteFriendships(userId, friendId)")
    void removeFriend_whenUserIsRequester_deletes() {
        long userId = 1L;
        long friendId = 2L;

        when(userStorage.existsById(userId)).thenReturn(true);
        when(userStorage.existsById(friendId)).thenReturn(true);

        // requesterId = userId
        FriendShipsDto friendship = new FriendShipsDto(userId, friendId, false, false);
        when(friendsStorage.findFriendship(userId, friendId)).thenReturn(Optional.of(friendship));

        friendsService.removeFriend(userId, friendId);

        verify(friendsStorage).deleteFriendships(userId, friendId);
        verify(friendsStorage, never()).updateFriendships(any());
    }

    @Test
    @DisplayName("removeFriend – userId не requester: вызывается updateFriendships(friendId, userId, true, true)")
    void removeFriend_whenUserIsAddressee_updates() {
        long userId = 1L;
        long friendId = 2L;

        when(userStorage.existsById(userId)).thenReturn(true);
        when(userStorage.existsById(friendId)).thenReturn(true);

        // requesterId = friendId (то есть userId не requester)
        FriendShipsDto friendship = new FriendShipsDto(friendId, userId, false, false);
        when(friendsStorage.findFriendship(userId, friendId)).thenReturn(Optional.of(friendship));

        friendsService.removeFriend(userId, friendId);

        ArgumentCaptor<FriendShipsDto> captor = ArgumentCaptor.forClass(FriendShipsDto.class);
        verify(friendsStorage).updateFriendships(captor.capture());
        FriendShipsDto passed = captor.getValue();

        assertEquals(friendId, passed.requesterId());
        assertEquals(userId, passed.addresseeId());
        assertTrue(passed.addresseeDeleted());
        assertTrue(passed.status());

        verify(friendsStorage, never()).deleteFriendships(anyLong(), anyLong());
    }

    @Test
    @DisplayName("getFriends – ошибка: userId не существует")
    void getFriends_throws_whenUserNotFound() {
        when(userStorage.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> friendsService.getFriends(1L));

        verifyNoInteractions(friendsStorage);
    }

    @Test
    @DisplayName("getFriends – успех: возвращает список UserResponseDto по ids друзей")
    void getFriends_success() {
        long userId = 1L;
        Set<Long> friendIds = Set.of(10L, 11L);

        when(userStorage.existsById(userId)).thenReturn(true);
        when(friendsStorage.getFriends(userId)).thenReturn(friendIds);

        User u10 = new User();
        u10.setId(10L);
        u10.setEmail("a@a.com");
        u10.setLogin("a");
        u10.setName("A");
        u10.setBirthday(LocalDate.of(1990, 1, 1));

        User u11 = new User();
        u11.setId(11L);
        u11.setEmail("b@b.com");
        u11.setLogin("b");
        u11.setName("B");
        u11.setBirthday(LocalDate.of(1991, 1, 1));

        when(userStorage.getByIds(friendIds)).thenReturn(List.of(u10, u11));

        List<UserResponseDto> result = friendsService.getFriends(userId);

        assertEquals(2, result.size());
        assertEquals(10L, result.get(0).id());
        assertEquals(11L, result.get(1).id());

        verify(friendsStorage).getFriends(userId);
        verify(userStorage).getByIds(friendIds);
    }

    @Test
    @DisplayName("getMutualFriends – ошибка: userId не существует")
    void getMutualFriends_throws_whenUserNotFound() {
        when(userStorage.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> friendsService.getMutualFriends(1L, 2L));

        verify(userStorage, never()).existsById(2L);
        verifyNoInteractions(friendsStorage);
    }

    @Test
    @DisplayName("getMutualFriends – ошибка: friendId не существует")
    void getMutualFriends_throws_whenFriendNotFound() {
        when(userStorage.existsById(1L)).thenReturn(true);
        when(userStorage.existsById(2L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> friendsService.getMutualFriends(1L, 2L));

        verifyNoInteractions(friendsStorage);
    }

    @Test
    @DisplayName("getMutualFriends – успех: пересечение множеств друзей и маппинг в UserResponseDto")
    void getMutualFriends_success() {
        long userId = 1L;
        long friendId = 2L;

        when(userStorage.existsById(userId)).thenReturn(true);
        when(userStorage.existsById(friendId)).thenReturn(true);

        Set<Long> userFriends = Set.of(10L, 11L, 12L);
        Set<Long> friendFriends = Set.of(11L, 12L, 13L);

        when(friendsStorage.getFriends(userId)).thenReturn(userFriends);
        when(friendsStorage.getFriends(friendId)).thenReturn(friendFriends);

        Set<Long> mutual = Set.of(11L, 12L);

        User u11 = new User();
        u11.setId(11L);
        u11.setEmail("b@b.com");
        u11.setLogin("b");
        u11.setName("B");
        u11.setBirthday(LocalDate.of(1991, 1, 1));
        User u12 = new User();
        u12.setId(12L);
        u12.setEmail("c@c.com");
        u12.setLogin("c");
        u12.setName("C");
        u12.setBirthday(LocalDate.of(1992, 2, 2));
        when(userStorage.getByIds(mutual)).thenReturn(List.of(u11, u12));

        List<UserResponseDto> result = friendsService.getMutualFriends(userId, friendId);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(u -> u.id() == 11L));
        assertTrue(result.stream().anyMatch(u -> u.id() == 12L));

        verify(userStorage).getByIds(mutual);
    }
}
