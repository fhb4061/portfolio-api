package aus.tane.portfolio.controllers;

import aus.tane.portfolio.dto.Todo;
import aus.tane.portfolio.dto.TodoStatus;
import aus.tane.portfolio.dto.User;
import aus.tane.portfolio.dto.request.TodoRequest;
import aus.tane.portfolio.errors.NotFoundException;
import aus.tane.portfolio.repositories.TodoRepository;
import aus.tane.portfolio.repositories.UserRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoController(TodoRepository todoRepository, UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Todo> listTodos(@RequestParam(required = false) Long userId) {
        if (userId == null) {
            return todoRepository.findAll();
        }
        return todoRepository.findByUserId(userId);
    }

    @GetMapping("/{id}")
    public Todo getTodo(@PathVariable Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Todo not found"));
    }

    @PostMapping
    public Todo createTodo(@Valid @RequestBody TodoRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        Todo todo = new Todo(null, request.title(), request.status(), user);
        return todoRepository.save(todo);
    }

    @PutMapping("/{id}")
    public Todo updateTodo(@PathVariable Long id, @Valid @RequestBody TodoRequest request) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Todo not found"));
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        todo.setTitle(request.title());
        todo.setStatus(request.status());
        todo.setUser(user);
        return todoRepository.save(todo);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id) {
        if (!todoRepository.existsById(id)) {
            throw new NotFoundException("Todo not found");
        }
        todoRepository.deleteById(id);
    }
}
