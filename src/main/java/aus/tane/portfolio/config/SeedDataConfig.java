package aus.tane.portfolio.config;

import aus.tane.portfolio.dto.Todo;
import aus.tane.portfolio.dto.TodoStatus;
import aus.tane.portfolio.dto.User;
import aus.tane.portfolio.repositories.TodoRepository;
import aus.tane.portfolio.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("dev")
public class SeedDataConfig {
    @Bean
    CommandLineRunner seedData(UserRepository userRepository, TodoRepository todoRepository) {
        return args -> {
            if (userRepository.count() > 0 || todoRepository.count() > 0) {
                return;
            }

            User alice = userRepository.save(new User(null, "Alice Chen", "alice@example.com", null));
            User sam = userRepository.save(new User(null, "Sam Patel", "sam@example.com", null));

            List<Todo> todos = List.of(
                    new Todo(null, "Review Spring Data JPA basics", TodoStatus.IN_PROGRESS, alice),
                    new Todo(null, "Practice CRUD endpoints", TodoStatus.PENDING, alice),
                    new Todo(null, "Prepare interview questions", TodoStatus.DONE, sam)
            );

            todoRepository.saveAll(todos);
        };
    }
}
