package code_wave.todo.repository;

import code_wave.todo.oauth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRespository extends JpaRepository<User, Long> {
}
