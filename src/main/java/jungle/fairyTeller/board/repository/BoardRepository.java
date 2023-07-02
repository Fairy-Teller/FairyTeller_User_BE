package jungle.fairyTeller.board.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jungle.fairyTeller.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<BoardEntity, Integer> {
    Optional<BoardEntity> findByBoardId(Integer boardId);
    Page<BoardEntity> findByTitleContainingIgnoreCaseOrAuthor_NicknameContainingIgnoreCase(String titleKeyword, String authorKeyword, Pageable pageable);
    Page<BoardEntity> findByAuthor_NicknameContainingIgnoreCase(String author, Pageable pageable);
    Page<BoardEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    @Query("SELECT b FROM BoardEntity b WHERE b.createdDatetime BETWEEN ?1 AND ?2 ORDER BY b.heartCount DESC, b.createdDatetime DESC")
    List<BoardEntity> findPopularBoardsByHeartCount(Date startDate, Date endDate, int limit);
    @Query("SELECT b FROM BoardEntity b WHERE b.createdDatetime BETWEEN :startDate AND :endDate")
    List<BoardEntity> getBoardsBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
