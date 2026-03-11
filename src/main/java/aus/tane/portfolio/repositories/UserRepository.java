package aus.tane.portfolio.repositories;

import aus.tane.portfolio.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
