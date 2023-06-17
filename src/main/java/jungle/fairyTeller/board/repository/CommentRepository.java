package jungle.fairyTeller.board.repository;

import jungle.fairyTeller.board.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    List<CommentEntity> findByBoardId(Integer boardId);
}
