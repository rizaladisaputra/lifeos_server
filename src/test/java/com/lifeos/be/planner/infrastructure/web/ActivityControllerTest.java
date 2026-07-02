package com.lifeos.be.planner.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifeos.be.core.exception.GlobalExceptionHandler;
import com.lifeos.be.core.security.JwtService;
import com.lifeos.be.planner.application.dto.ActivityDto;
import com.lifeos.be.planner.application.service.ActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Test untuk ActivityController menggunakan @WebMvcTest.
 * <p>
 * Menguji lapisan HTTP (web) secara terisolasi tanpa menjalankan
 * full Spring context atau database. Hanya controller + security + exception handler
 * yang di-load. Service layer di-mock menggunakan Mockito.
 * </p>
 */
@WebMvcTest(ActivityController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("ActivityController Integration Tests (@WebMvcTest)")
class ActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ActivityService activityService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    private ActivityDto sampleDto;

    @BeforeEach
    void setUp() {
        sampleDto = ActivityDto.builder()
                .id(UUID.randomUUID())
                .time("09:00")
                .emoji("📚")
                .title("Baca Buku")
                .category("LEARNING")
                .isCompleted(false)
                .date(LocalDate.now())
                .build();
    }

    @Nested
    @DisplayName("GET /api/activities")
    class GetActivitiesTests {

        @Test
        @WithMockUser
        @DisplayName("should return 200 OK with list of activities")
        void shouldReturn200WithActivities() throws Exception {
            when(activityService.getActivitiesByDate(LocalDate.now()))
                    .thenReturn(List.of(sampleDto));

            mockMvc.perform(get("/api/activities")
                            .param("date", LocalDate.now().toString())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].title", is("Baca Buku")))
                    .andExpect(jsonPath("$[0].time", is("09:00")));
        }

        @Test
        @DisplayName("should return 401 Unauthorized when not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/api/activities")
                            .param("date", LocalDate.now().toString()))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("POST /api/activities")
    class CreateActivityTests {

        @Test
        @WithMockUser
        @DisplayName("should return 200 OK when creating valid activity")
        void shouldCreateActivitySuccessfully() throws Exception {
            ActivityDto request = ActivityDto.builder()
                    .time("14:00").title("Olahraga")
                    .emoji("🏃").date(LocalDate.now())
                    .build();

            when(activityService.createActivity(any(ActivityDto.class)))
                    .thenReturn(request);

            mockMvc.perform(post("/api/activities")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title", is("Olahraga")));
        }

        @Test
        @WithMockUser
        @DisplayName("should return 400 Bad Request when title is blank")
        void shouldReturn400WhenTitleIsBlank() throws Exception {
            ActivityDto invalidRequest = ActivityDto.builder()
                    .time("14:00").title("") // Invalid: kosong
                    .date(LocalDate.now()).build();

            mockMvc.perform(post("/api/activities")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title", is("Validation Failed")))
                    .andExpect(jsonPath("$.errors.title", notNullValue()));
        }

        @Test
        @WithMockUser
        @DisplayName("should return 409 Conflict when business rule (time conflict) is violated")
        void shouldReturn409WhenTimeConflict() throws Exception {
            ActivityDto request = ActivityDto.builder()
                    .time("09:05").title("Konflik").emoji("❌")
                    .date(LocalDate.now()).build();

            when(activityService.createActivity(any(ActivityDto.class)))
                    .thenThrow(new IllegalStateException("Time conflict detected. Activities must be at least 15 minutes apart."));

            mockMvc.perform(post("/api/activities")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.title", is("Business Rule Violation")))
                    .andExpect(jsonPath("$.detail", containsString("Time conflict")));
        }
    }

    @Nested
    @DisplayName("PUT /api/activities/{id}/toggle")
    class ToggleActivityTests {

        @Test
        @WithMockUser
        @DisplayName("should return 200 OK with toggled activity")
        void shouldToggleActivitySuccessfully() throws Exception {
            UUID id = sampleDto.getId();
            ActivityDto toggled = ActivityDto.builder()
                    .id(id).time("09:00").title("Baca Buku")
                    .emoji("📚").isCompleted(true).date(LocalDate.now())
                    .build();

            when(activityService.toggleActivity(eq(id))).thenReturn(toggled);

            mockMvc.perform(put("/api/activities/{id}/toggle", id)
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isCompleted", is(true)));
        }

        @Test
        @WithMockUser
        @DisplayName("should return 403 Forbidden when toggling another user's activity")
        void shouldReturn403WhenToggleOtherUserActivity() throws Exception {
            UUID id = UUID.randomUUID();
            when(activityService.toggleActivity(eq(id)))
                    .thenThrow(new SecurityException("You do not have permission to modify this activity"));

            mockMvc.perform(put("/api/activities/{id}/toggle", id)
                            .with(csrf()))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.title", is("Access Denied")));
        }
    }

    @Nested
    @DisplayName("DELETE /api/activities/{id}")
    class DeleteActivityTests {

        @Test
        @WithMockUser
        @DisplayName("should return 204 No Content on successful delete")
        void shouldDeleteActivitySuccessfully() throws Exception {
            UUID id = sampleDto.getId();
            doNothing().when(activityService).deleteActivity(id);

            mockMvc.perform(delete("/api/activities/{id}", id)
                            .with(csrf()))
                    .andExpect(status().isNoContent());
        }
    }
}
