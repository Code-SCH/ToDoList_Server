package code_wave.todo.repository;

import code_wave.todo.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findBySnsId(String id);
    Optional<Member> findByEmail(String email);
}
