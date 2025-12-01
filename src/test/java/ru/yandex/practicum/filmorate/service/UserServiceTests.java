package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {
    @Mock
    UserStorage userStorage;

    @InjectMocks
    UserService userService;

    @Test
    void createUserDelegatesToStorage() {
        User user = new User();
        when(userStorage.createUser(user)).thenReturn(user);

        User created = userService.createUser(user);

        assertThat(created).isSameAs(user);
        verify(userStorage).createUser(user);
    }

    @Test
    void updateUserRequiresExistingUser() {
        User user = new User();
        user.setId(5L);

        when(userStorage.getUserById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(user))
                .isInstanceOf(UserNotFoundException.class);

        verify(userStorage, never()).updateUser(any());
    }

    @Test
    void updateUserDelegatesAfterExistenceCheck() {
        User user = new User();
        user.setId(8L);
        when(userStorage.getUserById(8L)).thenReturn(Optional.of(user));
        when(userStorage.updateUser(user)).thenReturn(user);

        User updated = userService.updateUser(user);

        assertThat(updated).isSameAs(user);
        verify(userStorage).updateUser(user);
    }

    @Test
    void getAllUsersDelegatesToStorage() {
        User user = new User();
        user.setId(5L);

        User user2 = new User();
        user2.setId(6L);

        when(userStorage.getAllUsers()).thenReturn(List.of(user, user2));

        assertThat(userService.getAllUsers()).isEqualTo(List.of(user, user2));
        verify(userStorage).getAllUsers();
    }

    @Test
    void getFriendsResolvesFriendEntitiesFromIds() {
        User user = new User();
        user.setId(1L);
        user.getFriends().addAll(Set.of(2L, 3L));

        User friendOne = new User();
        friendOne.setId(2L);

        User friendTwo = new User();
        friendTwo.setId(3L);

        when(userStorage.getUserById(1L)).thenReturn(Optional.of(user));
        when(userStorage.getUserById(2L)).thenReturn(Optional.of(friendOne));
        when(userStorage.getUserById(3L)).thenReturn(Optional.of(friendTwo));

        Set<User> friends = userService.getFriends(1L);

        assertThat(friends).containsExactlyInAnyOrder(friendOne, friendTwo);
        verify(userStorage).getUserById(1L);
        verify(userStorage).getUserById(2L);
        verify(userStorage).getUserById(3L);
    }

    @Test
    void getFriendsSkipsMissingFriendEntries() {
        User user = new User();
        user.setId(4L);
        user.getFriends().addAll(Set.of(5L, 6L));

        User existingFriend = new User();
        existingFriend.setId(5L);

        when(userStorage.getUserById(4L)).thenReturn(Optional.of(user));
        when(userStorage.getUserById(5L)).thenReturn(Optional.of(existingFriend));
        when(userStorage.getUserById(6L)).thenReturn(Optional.empty());

        Set<User> friends = userService.getFriends(4L);

        assertThat(friends).containsExactly(existingFriend);
        verify(userStorage).getUserById(4L);
        verify(userStorage).getUserById(5L);
        verify(userStorage).getUserById(6L);
    }

    @Test
    void getFriendsThrowsWhenUserMissing() {
        when(userStorage.getUserById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getFriends(42L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userStorage).getUserById(42L);
        verifyNoMoreInteractions(userStorage);
    }

    @Test
    void addFriendWhenUserAndFriendExists() {
        User user = new User();
        user.setId(1L);

        User friend = new User();
        friend.setId(2L);

        when(userStorage.getUserById(1L)).thenReturn(Optional.of(user));
        when(userStorage.getUserById(2L)).thenReturn(Optional.of(friend));

        userService.addFriend(user.getId(), friend.getId());

        assertEquals(user.getFriends(), Set.of(2L));
    }

    @Test
    void addFriendWhenUserNotExist() {

        when(userStorage.getUserById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.addFriend(1L, 2L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userStorage).getUserById(1L);
    }

    @Test
    void addFriendThrowsWhenUserExistButNotFriend() {
        User user = new User();
        user.setId(1L);

        when(userStorage.getUserById(1L)).thenReturn(Optional.of(user));
        when(userStorage.getUserById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.addFriend(1L, 2L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userStorage).getUserById(1L);
        verify(userStorage).getUserById(2L);
    }

    @Test
    void removeFriendWhenUserAndFriendExists() {
        User user = new User();
        user.setId(1L);
        user.setFriends(new HashSet<>(Set.of(2L, 3L)));

        User friend = new User();
        friend.setId(2L);

        User friend2 = new User();
        friend2.setId(3L);

        when(userStorage.getUserById(1L)).thenReturn(Optional.of(user));
        when(userStorage.getUserById(2L)).thenReturn(Optional.of(friend));

        userService.removeFriend(user.getId(), friend.getId());

        assertEquals(user.getFriends(), Set.of(3L));
    }

    @Test
    void removeFriendWhenUserNotExist() {

        when(userStorage.getUserById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.removeFriend(1L, 2L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userStorage).getUserById(1L);
    }

    @Test
    void removeFriendThrowsWhenUserExistButNotFriend() {
        User user = new User();
        user.setId(1L);

        when(userStorage.getUserById(1L)).thenReturn(Optional.of(user));
        when(userStorage.getUserById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.addFriend(1L, 2L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userStorage).getUserById(1L);
        verify(userStorage).getUserById(2L);
    }


    @Test
    void getMutualFriendsWhenUserAndFriendExists() {
        User user = new User();
        user.setId(1L);
        user.setFriends(new HashSet<>(Set.of(2L, 3L, 4L)));

        User friend = new User();
        friend.setId(2L);
        friend.setFriends(new HashSet<>(Set.of(3L, 4L)));

        User mutualFriend1 = new User();
        mutualFriend1.setId(3L);

        User mutualFriend2 = new User();
        mutualFriend2.setId(4L);

        when(userStorage.getUserById(1L)).thenReturn(Optional.of(user));
        when(userStorage.getUserById(2L)).thenReturn(Optional.of(friend));
        when(userStorage.getUserById(3L)).thenReturn(Optional.of(mutualFriend1));
        when(userStorage.getUserById(4L)).thenReturn(Optional.of(mutualFriend2));

        Set<User> mutualFriends = userService.getMutualFriends(user.getId(), friend.getId());

        assertEquals(mutualFriends, Set.of(mutualFriend1, mutualFriend2));

        verify(userStorage).getUserById(1L);
        verify(userStorage).getUserById(2L);
        verify(userStorage).getUserById(3L);
        verify(userStorage).getUserById(4L);
        verifyNoMoreInteractions(userStorage);
    }

    @Test
    void getMutualFriendsWhenUserExistButNotFriend() {
        User user = new User();
        user.setId(1L);
        user.setFriends(new HashSet<>(Set.of(2L, 3L, 4L)));

        when(userStorage.getUserById(1L)).thenReturn(Optional.of(user));
        when(userStorage.getUserById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getMutualFriends(1L, 2L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userStorage).getUserById(1L);
        verify(userStorage).getUserById(2L);
    }

    @Test
    void getMutualFriendsWhenFriendExistButNotUser() {
        when(userStorage.getUserById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getMutualFriends(1L, 2L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userStorage).getUserById(1L);
    }

}
