package aus.tane.portfolio.repositories;

import aus.tane.portfolio.dto.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void saveAndFindUser() {
        User saved = userRepository.save(new User(null, "Repo User", "repo@example.com", null));

        assertThat(saved.getId()).isNotNull();
        assertThat(userRepository.findById(saved.getId()))
                .isPresent()
                .get()
                .extracting(User::getEmail)
                .isEqualTo("repo@example.com");
    }
}
