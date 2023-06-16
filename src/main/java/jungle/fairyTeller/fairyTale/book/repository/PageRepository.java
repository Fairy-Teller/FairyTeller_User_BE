package jungle.fairyTeller.fairyTale.book.repository;

import jungle.fairyTeller.fairyTale.book.entity.PageEntity;
import jungle.fairyTeller.fairyTale.book.entity.PageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageRepository extends JpaRepository<PageEntity, PageId> {
    PageEntity findByPageNo(PageId pageId);

    List<PageEntity> findAllByBookBookId(Integer bookId);
}

