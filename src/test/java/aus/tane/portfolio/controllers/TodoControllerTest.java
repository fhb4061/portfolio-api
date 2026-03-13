package aus.tane.portfolio.controllers;

import aus.tane.portfolio.dto.Todo;
import aus.tane.portfolio.dto.TodoStatus;
import aus.tane.portfolio.dto.User;
import aus.tane.portfolio.dto.request.TodoRequest;
import aus.tane.portfolio.repositories.TodoRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
class TodoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TodoRepository todoRepository;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void listTodos_returnsAllWhenNoFilter() throws Exception {
        User owner = new User(1L, "Sam", "sam@example.com", Instant.parse("2026-03-13T00:00:00Z"));
        Todo todo = new Todo(10L, "Practice CRUD", TodoStatus.PENDING, owner);
        when(todoRepository.findAll()).thenReturn(List.of(todo));

        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].title").value("Practice CRUD"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void listTodos_filtersByUserId() throws Exception {
        User owner = new User(2L, "Alice", "alice@example.com", Instant.parse("2026-03-13T00:00:00Z"));
        Todo todo = new Todo(11L, "Study JPA", TodoStatus.IN_PROGRESS, owner);
        when(todoRepository.findByUserId(2L)).thenReturn(List.of(todo));

        mockMvc.perform(get("/todos").param("userId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(11))
                .andExpect(jsonPath("$[0].status").value("IN_PROGRESS"));
    }

    @Test
    void getTodo_returnsTodo() throws Exception {
        User owner = new User(3L, "Bea", "bea@example.com", Instant.parse("2026-03-13T00:00:00Z"));
        Todo todo = new Todo(12L, "Review endpoints", TodoStatus.DONE, owner);
        when(todoRepository.findById(12L)).thenReturn(Optional.of(todo));

        mockMvc.perform(get("/todos/12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.title").value("Review endpoints"))
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    void getTodo_missing_returnsNotFound() throws Exception {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/todos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTodo_persistsAndReturnsTodo() throws Exception {
        User owner = new User(4L, "Pat", "pat@example.com", Instant.parse("2026-03-13T00:00:00Z"));
        Todo saved = new Todo(20L, "Build API", TodoStatus.PENDING, owner);
        when(userRepository.findById(4L)).thenReturn(Optional.of(owner));
        when(todoRepository.save(any(Todo.class))).thenReturn(saved);

        String payload = objectMapper.writeValueAsString(new TodoRequest("Build API", TodoStatus.PENDING, 4L));

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.title").value("Build API"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void updateTodo_updatesAndReturnsTodo() throws Exception {
        User owner = new User(5L, "Lee", "lee@example.com", Instant.parse("2026-03-13T00:00:00Z"));
        Todo existing = new Todo(21L, "Old", TodoStatus.PENDING, owner);
        Todo updated = new Todo(21L, "Updated", TodoStatus.DONE, owner);
        when(todoRepository.findById(21L)).thenReturn(Optional.of(existing));
        when(userRepository.findById(5L)).thenReturn(Optional.of(owner));
        when(todoRepository.save(any(Todo.class))).thenReturn(updated);

        String payload = objectMapper.writeValueAsString(new TodoRequest("Updated", TodoStatus.DONE, 5L));

        mockMvc.perform(put("/todos/21")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(21))
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    void deleteTodo_deletesWhenExists() throws Exception {
        when(todoRepository.existsById(22L)).thenReturn(true);
        doNothing().when(todoRepository).deleteById(22L);

        mockMvc.perform(delete("/todos/22"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTodo_missing_returnsNotFound() throws Exception {
        when(todoRepository.existsById(23L)).thenReturn(false);

        mockMvc.perform(delete("/todos/23"))
                .andExpect(status().isNotFound());
    }
}
