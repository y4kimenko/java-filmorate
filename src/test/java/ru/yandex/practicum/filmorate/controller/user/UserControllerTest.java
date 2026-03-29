package ru.yandex.practicum.filmorate.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.dto.user.request.UserRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.user.request.UserRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.user.response.UserResponseDto;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.web.controller.user.UserController;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @MockBean
    UserService userService;
    @MockBean
    FilmService filmService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /users  возвращает HTTP-ответ со статусом 200 OK с созданным user")
    void createUser_ReturnsOkWhenPayloadValid() throws Exception {
        UserRequestCreateDto requestDto = new UserRequestCreateDto(
                "mail@example.com",
                "login",
                "User Name",
                LocalDate.of(1990, 1, 1)
        );


        UserResponseDto responseDto = new UserResponseDto(
                1L,
                "mail@example.com",
                "login",
                "User Name",
                LocalDate.of(1990, 1, 1)
        );


        when(userService.create(any(UserRequestCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
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
        UserRequestCreateDto requestDto = new UserRequestCreateDto(
                "mail example.com",
                "login",
                "User Name",
                LocalDate.of(1990, 1, 1)
        );


        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации входных данных"))
                .andExpect(jsonPath("$.errors.email").value("E-mail  is incorrect"));

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("PUT /users возвращает HTTP-ответ со статусом 200 OK с обновленным user")
    void updateUser_ReturnsOkWhenPayloadValid() throws Exception {
        UserRequestUpdateDto requestDto = new UserRequestUpdateDto(
                1L,
                "mail@example.com",
                "login",
                "User Name",
                LocalDate.of(1990, 1, 1)
        );

        UserResponseDto responseDto = new UserResponseDto(
                1L,
                "mail@example.com",
                "login",
                "User Name",
                LocalDate.of(1990, 1, 1)
        );

        when(userService.update(any(UserRequestUpdateDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("mail@example.com"))
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.name").value("User Name"))
                .andExpect(jsonPath("$.birthday").value("1990-01-01"));

        verify(userService).update(any(UserRequestUpdateDto.class));
    }

    @Test
    @DisplayName("GET /users возвращает HTTP-ответ со статусом 200 OK и списком пользователей")
    void getAllUsers_ReturnsOkWithPayload() throws Exception {
        UserResponseDto responseDto = new UserResponseDto(
                1L,
                "mail@example.com",
                "login",
                "User Name",
                LocalDate.of(1990, 1, 1)
        );

        when(userService.getAll()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("mail@example.com"))
                .andExpect(jsonPath("$[0].login").value("login"))
                .andExpect(jsonPath("$[0].name").value("User Name"))
                .andExpect(jsonPath("$[0].birthday").value("1990-01-01"));

        verify(userService).getAll();
    }


}