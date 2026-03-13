package aus.tane.portfolio.controllers;

import aus.tane.portfolio.dto.User;
import aus.tane.portfolio.dto.request.UserPatchRequest;
import aus.tane.portfolio.dto.request.UserRequest;
import aus.tane.portfolio.repositories.UserRepository;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void listUsers_returnsUsers() throws Exception {
        User user = new User(1L, "Sam Patel", "sam@example.com", Instant.parse("2026-03-13T00:00:00Z"));
        when(userRepository.findAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Sam Patel"))
                .andExpect(jsonPath("$[0].email").value("sam@example.com"));
    }

    @Test
    void getUser_returnsUser() throws Exception {
        User user = new User(2L, "Alice Chen", "alice@example.com", Instant.parse("2026-03-13T00:00:00Z"));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Alice Chen"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void getUser_missing_returnsNotFound() throws Exception {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_persistsAndReturnsUser() throws Exception {
        User saved = new User(3L, "New User", "new@example.com", Instant.parse("2026-03-13T00:00:00Z"));
        when(userRepository.save(any(User.class))).thenReturn(saved);

        String payload = objectMapper.writeValueAsString(new UserRequest("New User", "new@example.com"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("New User"))
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    void updateUser_updatesAndReturnsUser() throws Exception {
        User existing = new User(4L, "Old Name", "old@example.com", Instant.parse("2026-03-13T00:00:00Z"));
        User updated = new User(4L, "Updated", "updated@example.com", Instant.parse("2026-03-13T00:00:00Z"));
        when(userRepository.findById(4L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenReturn(updated);

        String payload = objectMapper.writeValueAsString(new UserRequest("Updated", "updated@example.com"));

        mockMvc.perform(put("/users/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void patchUser_updatesOnlyProvidedFields() throws Exception {
        User existing = new User(5L, "Before", "before@example.com", Instant.parse("2026-03-13T00:00:00Z"));
        User patched = new User(5L, "After", "before@example.com", Instant.parse("2026-03-13T00:00:00Z"));
        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenReturn(patched);

        String payload = objectMapper.writeValueAsString(new UserPatchRequest("After", null));

        mockMvc.perform(patch("/users/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("After"))
                .andExpect(jsonPath("$.email").value("before@example.com"));
    }

    @Test
    void deleteUser_deletesWhenExists() throws Exception {
        when(userRepository.existsById(7L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(7L);

        mockMvc.perform(delete("/users/7"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_missing_returnsNotFound() throws Exception {
        when(userRepository.existsById(8L)).thenReturn(false);

        mockMvc.perform(delete("/users/8"))
                .andExpect(status().isNotFound());
    }
}
