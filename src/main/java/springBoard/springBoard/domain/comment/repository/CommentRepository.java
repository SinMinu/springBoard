package springBoard.springBoard.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springBoard.springBoard.domain.comment.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
