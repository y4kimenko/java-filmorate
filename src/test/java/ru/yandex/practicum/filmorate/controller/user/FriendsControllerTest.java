package ru.yandex.practicum.filmorate.controller.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FriendsController.class)
class FriendsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("PUT /users/{id}/friends/{friendId} возвращает HTTP-ответ со статусом 200")
    void addFriend_ReturnsOkAndDelegatesToService() throws Exception {
        mockMvc.perform(put("/users/{id}/friends/{friendId}", 1L, 2L))
                .andExpect(status().isOk());

        verify(userService).addFriend(1L, 2L);
    }

    @Test
    @DisplayName("PUT /users/{id}/friends/{friendId} возвращает HTTP-ответ со статусом 400 и описанием ошибок 'Ошибка валидации параметров' и их подробное описание")
    void addFriend_ReturnsBadRequest_WhenIdsNegative() throws Exception {
        mockMvc.perform(put("/users/{id}/friends/{friendId}", -1L, -2L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации параметров"))
                .andExpect(jsonPath("$.errors.id").value("id пользователя не может быть отрицательным"))
                .andExpect(jsonPath("$.errors.friendId").value("id друга не может быть отрицательным"));

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("DELETE /users/{id}/friends/{friendId} возвращает HTTP-ответ со статусом 200")
    void removeFriend_ReturnsOkAndDelegatesToService() throws Exception {
        mockMvc.perform(delete("/users/{id}/friends/{friendId}", 5L, 6L))
                .andExpect(status().isOk());

        verify(userService).removeFriend(5L, 6L);
    }

    @Test
    @DisplayName("DELETE /users/{id}/friends/{friendId} возвращает HTTP-ответ со статусом 400 и описанием ошибок 'Ошибка валидации параметров' и их подробное описание")
    void removeFriend_ReturnsBadRequest_WhenFriendIdNegative() throws Exception {
        mockMvc.perform(delete("/users/{id}/friends/{friendId}", 7L, -4L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации параметров"))
                .andExpect(jsonPath("$.errors.friendId").value("id друга не может быть отрицательным"));

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("GET /users/{id}/friends возвращает HTTP-ответ со статусом 200 и Collection<User> друзей")
    void getFriends_ReturnsOkWithPayload() throws Exception {
        User friend = new User();
        friend.setId(10L);
        friend.setEmail("friend@example.com");
        friend.setLogin("friend");
        friend.setName("Friend Name");
        friend.setBirthday(LocalDate.of(1980, 12, 12));

        when(userService.getFriends(3L)).thenReturn(Set.of(friend));

        mockMvc.perform(get("/users/{id}/friends", 3L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].email").value("friend@example.com"))
                .andExpect(jsonPath("$[0].login").value("friend"))
                .andExpect(jsonPath("$[0].name").value("Friend Name"))
                .andExpect(jsonPath("$[0].birthday").value("1980-12-12"));

        verify(userService).getFriends(3L);
    }

    @Test
    @DisplayName("GET /users/{id}/friends возвращает HTTP-ответ со статусом 400 и описанием ошибки 'id пользователя не может быть отрицательным'")
    void getFriends_ReturnsBadRequest_WhenIdNegative() throws Exception {
        mockMvc.perform(get("/users/{id}/friends", -3L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации параметров"))
                .andExpect(jsonPath("$.errors.id").value("id пользователя не может быть отрицательным"));

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("GET /users/{id}/friends возвращает HTTP-ответ со статусом 200 и Collection<User>")
    void getMutualFriends_ReturnsOkWithPayload() throws Exception {
        User mutual = new User();
        mutual.setId(11L);
        mutual.setEmail("mutual@example.com");
        mutual.setLogin("mutual");
        mutual.setName("Mutual Friend");
        mutual.setBirthday(LocalDate.of(1988, 8, 8));

        when(userService.getMutualFriends(1L, 2L)).thenReturn(Set.of(mutual));

        mockMvc.perform(get("/users/{id}/friends/common/{friendId}", 1L, 2L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(11))
                .andExpect(jsonPath("$[0].email").value("mutual@example.com"))
                .andExpect(jsonPath("$[0].login").value("mutual"))
                .andExpect(jsonPath("$[0].name").value("Mutual Friend"))
                .andExpect(jsonPath("$[0].birthday").value("1988-08-08"));

        verify(userService).getMutualFriends(1L, 2L);
    }

    @Test
    @DisplayName("GET /users/{id}/friends/common/{friendId} возвращает HTTP-ответ со статусом 200 и Collection<User> общих друзей")
    void getMutualFriends_ReturnsBadRequest_WhenFriendIdNegative() throws Exception {
        mockMvc.perform(get("/users/{id}/friends/common/{friendId}", 1L, -2L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации параметров"))
                .andExpect(jsonPath("$.errors.friendId").value("id друга не может быть отрицательным"));

        verifyNoInteractions(userService);
    }
}