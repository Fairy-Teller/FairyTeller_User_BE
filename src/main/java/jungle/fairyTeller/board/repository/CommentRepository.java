package jungle.fairyTeller.board.repository;

import jungle.fairyTeller.board.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findAllByBoardId(Long boardId);
}