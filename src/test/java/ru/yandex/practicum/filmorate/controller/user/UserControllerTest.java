package ru.yandex.practicum.filmorate.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    UserService userService;


    @Test
    @DisplayName("POST /users  возвращает HTTP-ответ со статусом 200 OK с созданным user")
    void createUser_ReturnsOkWhenPayloadValid() throws Exception {
        User request = new User();
        request.setEmail("mail@example.com");
        request.setLogin("login");
        request.setName("User Name");
        request.setBirthday(LocalDate.of(1990, 1, 1));

        User created = new User();
        created.setId(1L);
        created.setEmail(request.getEmail());
        created.setLogin(request.getLogin());
        created.setName(request.getName());
        created.setBirthday(request.getBirthday());

        when(userService.createUser(any(User.class))).thenReturn(created);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("mail@example.com"))
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.name").value("User Name"))
                .andExpect(jsonPath("$.birthday").value("1990-01-01"));
    }

    @Test
    @DisplayName("POST /users  возвращает HTTP-ответ со статусом 400 и описанием ошибки 'E-mail  is incorrect'")
    void createUser_ReturnsBadRequestWhenEmailInvalid() throws Exception {
        User request = new User();
        request.setEmail("invalid_email");
        request.setLogin("login");
        request.setName("User Name");
        request.setBirthday(LocalDate.of(1990, 1, 1));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации входных данных"))
                .andExpect(jsonPath("$.errors.email").value("E-mail  is incorrect"));

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("PUT /users возвращает HTTP-ответ со статусом 200 OK с обновленным user")
    void updateUser_ReturnsOkWhenPayloadValid() throws Exception {
        User request = new User();
        request.setId(5L);
        request.setEmail("new@example.com");
        request.setLogin("newlogin");
        request.setName("New Name");
        request.setBirthday(LocalDate.of(1995, 5, 5));

        when(userService.updateUser(any(User.class))).thenReturn(request);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.login").value("newlogin"))
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.birthday").value("1995-05-05"));

        verify(userService).updateUser(any(User.class));
    }

    @Test
    @DisplayName("GET /users возвращает HTTP-ответ со статусом 200 OK и списком пользователей")
    void getAllUsers_ReturnsOkWithPayload() throws Exception {
        User user = new User();
        user.setId(2L);
        user.setEmail("user@example.com");
        user.setLogin("userlogin");
        user.setName("User");
        user.setBirthday(LocalDate.of(1985, 3, 3));

        when(userService.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].email").value("user@example.com"))
                .andExpect(jsonPath("$[0].login").value("userlogin"))
                .andExpect(jsonPath("$[0].name").value("User"))
                .andExpect(jsonPath("$[0].birthday").value("1985-03-03"));

        verify(userService).getAllUsers();
    }


}