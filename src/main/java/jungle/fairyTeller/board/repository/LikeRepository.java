package jungle.fairyTeller.board.repository;

import jungle.fairyTeller.board.entity.BoardEntity;
import jungle.fairyTeller.board.entity.LikeEntity;
import jungle.fairyTeller.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<LikeEntity, Integer> {
    boolean existsByBoardAndUser(BoardEntity board, UserEntity user);
    Optional<LikeEntity> findByBoardAndUser(BoardEntity board, UserEntity user);
}
