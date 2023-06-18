package jungle.fairyTeller.board.repository;

import jungle.fairyTeller.board.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    Page<CommentEntity> findByBoardBoardId(Integer boardId, Pageable pageable);
}