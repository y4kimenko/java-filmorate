package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTests {

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        var controller = new UserController();

        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        var conv = new MappingJackson2HttpMessageConverter(mapper);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setMessageConverters(conv)
                .build();
    }

    // ───────── Негативные кейсы валидации ─────────

    @Test
    void createUser_ShouldReturn400_WhenEmailBlank() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("email", "   "); // нарушает @NotBlank и @Email
        body.put("login", "user123");
        body.put("name", "User Name");
        body.put("birthday", "2000-01-01");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_ShouldReturn400_WhenLoginContainsSpaces() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("email", "user@example.com");
        body.put("login", "user name"); // нарушает @Pattern(\S+)
        body.put("name", "User Name");
        body.put("birthday", "2000-01-01");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_ShouldReturn400_WhenBirthdayInFuture() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("email", "user@example.com");
        body.put("login", "username");
        body.put("name", "User Name");
        body.put("birthday", "2999-01-01"); // нарушает @Past

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void createUser_ShouldReturnSavedUserWithId() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("email", "user@example.com");
        body.put("login", "username");
        body.put("name", "User Name");
        body.put("birthday", "2000-01-01");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("user@example.com")))
                .andExpect(jsonPath("$.login", is("username")))
                .andExpect(jsonPath("$.name", is("User Name")))
                .andExpect(jsonPath("$.birthday", is("2000-01-01")));
    }

    // ───────── Обновление ─────────

    @Test
    void updateUser_WhenExists_ShouldApplyChanges() throws Exception {
        // Создаём пользователя id=1
        Map<String, Object> create = Map.of(
                "email", "old@example.com",
                "login", "oldlogin",
                "name", "Old Name",
                "birthday", "1990-05-20"
        );
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(create)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        // Обновляем поля у id=1
        Map<String, Object> patchBody = Map.of(
                "id", 1,
                "email", "new@example.com",
                "login", "newlogin",
                "name", "New Name",
                "birthday", "1991-06-21"
        );

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(patchBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("new@example.com")))
                .andExpect(jsonPath("$.login", is("newlogin")))
                .andExpect(jsonPath("$.name", is("New Name")))
                .andExpect(jsonPath("$.birthday", is("1991-06-21")));
    }

    @Test
    void updateUser_WhenNotExists_ShouldPropagateException() throws Exception {
        var patchBody = Map.of(
                "id", 999,
                "email", "ghost@example.com",
                "login", "ghost",
                "name", "Ghost",
                "birthday", "1980-01-01"
        );

        ServletException thrown = assertThrows(ServletException.class, () ->
                mockMvc.perform(put("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(patchBody)))
                        .andReturn()
        );

        var root = thrown.getCause();
        assertNotNull(root);
        // если контроллер бросает твой NotFoundException
        assertTrue(root instanceof NotFoundException || root instanceof IllegalArgumentException);
        assertTrue(root.getMessage().contains("User with id=" + patchBody.get("id") + " not found"));
    }

    // ───────── Получение всех ─────────

    @Test
    void getAllUsers_ShouldReturnAllCreated() throws Exception {
        Map<String, Object> u1 = Map.of(
                "email", "a@example.com",
                "login", "userA",
                "name", "User A",
                "birthday", "1999-01-01"
        );
        Map<String, Object> u2 = Map.of(
                "email", "b@example.com",
                "login", "userB",
                "name", "User B",
                "birthday", "1998-02-02"
        );

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(u1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(u2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }
}
