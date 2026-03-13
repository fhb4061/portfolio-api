package aus.tane.portfolio.repositories;

import aus.tane.portfolio.dto.Todo;
import aus.tane.portfolio.dto.TodoStatus;
import aus.tane.portfolio.dto.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TodoRepositoryTest {
    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUserId_returnsUserTodos() {
        User owner = userRepository.save(new User(null, "Repo Owner", "owner@example.com", null));
        Todo todo = todoRepository.save(new Todo(null, "Repo Todo", TodoStatus.PENDING, owner));

        List<Todo> todos = todoRepository.findByUserId(owner.getId());

        assertThat(todos)
                .extracting(Todo::getId)
                .contains(todo.getId());
    }
}
