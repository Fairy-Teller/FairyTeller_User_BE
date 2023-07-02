package jungle.fairyTeller.fairyTale.book.repository;

import jungle.fairyTeller.fairyTale.book.entity.PageEntity;
import jungle.fairyTeller.fairyTale.book.entity.PageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface PageRepository extends JpaRepository<PageEntity, PageId> {
    PageEntity findByPageNo(PageId pageId);

    List<PageEntity> findAllByBookBookId(Integer bookId);
    @Modifying
    @Query("DELETE FROM PageEntity p WHERE p.book.id = :bookId")
    void deleteByBookId(@Param("bookId") Integer bookId);
}

