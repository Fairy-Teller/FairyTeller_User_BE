package jungle.fairyTeller.board.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jungle.fairyTeller.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<BoardEntity, Integer> {
    @Override
    Page<BoardEntity> findAll(Pageable pageable);
    Optional<BoardEntity> findByBoardId(Integer boardId);
}
