package jungle.fairyTeller.board.repository;

import jungle.fairyTeller.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<BoardEntity, Integer> {
    @Override
    List<BoardEntity> findAll();
}
