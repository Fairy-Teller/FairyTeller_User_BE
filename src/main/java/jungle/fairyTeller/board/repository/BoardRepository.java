package jungle.fairyTeller.board.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jungle.fairyTeller.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<BoardEntity, Integer> {
    Optional<BoardEntity> findByBoardId(Integer boardId);
    Page<BoardEntity> findByTitleContainingIgnoreCaseOrAuthor_NicknameContainingIgnoreCase(String titleKeyword, String authorKeyword, Pageable pageable);
    Page<BoardEntity> findByAuthor_NicknameContainingIgnoreCase(String author, Pageable pageable);
    Page<BoardEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
