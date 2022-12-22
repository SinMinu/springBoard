package springBoard.springBoard.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springBoard.springBoard.domain.member.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<Member> findByRefreshToken(String refreshToken);
}
