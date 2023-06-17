package jungle.fairyTeller.board.repository;

import jungle.fairyTeller.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Integer> {
    BoardEntity findByBoardId(Integer boardId);
}