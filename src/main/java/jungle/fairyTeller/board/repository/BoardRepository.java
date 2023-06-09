package jungle.fairyTeller.board.repository;

import jungle.fairyTeller.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<BoardEntity, Integer> {
    @Override
    List<BoardEntity> findAll();
    Optional<BoardEntity> findByBoardId(Integer boardId);
}
