package ru.yandex.practicum.filmorate.controller.film;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.service.likes.LikesService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LikesController.class)
class LikesControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikesService likesService;

    @Test
    @DisplayName("PUT /films/{id}/like/{userId} возвращает HTTP-ответ со статусом 200")
    void addUserLike_ReturnsOkAndDelegatesToService() throws Exception {
        mockMvc.perform(put("/films/{id}/like/{userId}", 1L, 2L))
                .andExpect(status().isOk());

        verify(likesService).addUserLike(1L, 2L);
    }

    @Test
    @DisplayName("PUT /films/{id}/like/{userId} возвращает HTTP-ответ со статусом 400 и сообщением 'id пользователя не может быть отрицательным' и 'id фильма не может быть отрицательным'")
    void addUserLike_ReturnsBadRequestWhenPathVariablesNegative() throws Exception {
        mockMvc.perform(put("/films/{id}/like/{userId}", -1, -5))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации параметров"))
                .andExpect(jsonPath("$.errors.filmId").value("id фильма не может быть отрицательным"))
                .andExpect(jsonPath("$.errors.userId").value("id пользователя не может быть отрицательным"));

        verifyNoInteractions(likesService);
    }


    @Test
    @DisplayName("DELETE /films/{id}/like/{userId} возвращает HTTP-ответ со статусом 200")
    void removeUserLike_ReturnsOkAndDelegatesToService() throws Exception {
        mockMvc.perform(delete("/films/{id}/like/{userId}", 3L, 4L))
                .andExpect(status().isOk());

        verify(likesService).removeUserLike(3L, 4L);
    }

    @Test
    @DisplayName("DELETE /films/{id}/like/{userId} возвращает HTTP-ответ со статусом 400 сообщением 'id пользователя не может быть отрицательным'")
    void removeUserLikeReturnsBadRequestWhenUserIdNegative() throws Exception {
        mockMvc.perform(delete("/films/{id}/like/{userId}", 10L, -2L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации параметров"))
                .andExpect(jsonPath("$.errors.userId").value("id пользователя не может быть отрицательным"));

        verifyNoInteractions(likesService);
    }

    @Test
    @DisplayName("DELETE /films/{id}/like/{userId} возвращает HTTP-ответ со статусом 400 сообщением 'id пользователя не может быть отрицательным'")
    void removeUserLikeReturnsBadRequestWhenFilmIdNegative() throws Exception {
        mockMvc.perform(delete("/films/{id}/like/{userId}", 10L, -2L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации параметров"))
                .andExpect(jsonPath("$.errors.userId").value("id пользователя не может быть отрицательным"));

        verifyNoInteractions(likesService);
    }

}