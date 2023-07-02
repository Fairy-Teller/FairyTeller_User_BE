package jungle.fairyTeller.fairyTale.book.repository;

import jungle.fairyTeller.fairyTale.book.entity.BookEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer> {

    @Override
    List<BookEntity> findAll();

    BookEntity findByBookId(Integer bookId);
    List<BookEntity> findAllByAuthor(Integer authorId);
    List<BookEntity> findAllByAuthorOrderByCreatedDatetimeDesc(Integer authorId);
    default BookEntity findLatestByAuthor(Integer authorId) {
        List<BookEntity> books = findAllByAuthorOrderByCreatedDatetimeDesc(authorId);
        return books.isEmpty() ? null : books.get(0);
    }
    @Query("SELECT COUNT(b) FROM BookEntity b WHERE b.author = :authorId AND b.editFinal = false ORDER BY b.lastModifiedDate DESC")
    int countByAuthorAndEditFinal(@Param("authorId")Integer authorId);
    List<BookEntity> findByAuthorAndEditFinalOrderByLastModifiedDateDesc(Integer authorId, boolean editFinal);
    void deleteById(@NotNull Integer bookId);
}
