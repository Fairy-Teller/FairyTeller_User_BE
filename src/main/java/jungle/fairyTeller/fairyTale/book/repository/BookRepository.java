package jungle.fairyTeller.fairyTale.book.repository;

import jungle.fairyTeller.fairyTale.book.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer> {
    @Override
    List<BookEntity> findAll();
    List<BookEntity> findAllByAuthor(Integer authorId);
    List<BookEntity> findAllByAuthorOrderByCreatedDatetimeDesc(Integer authorId);
    default BookEntity findLatestByAuthor(Integer authorId) {
        List<BookEntity> books = findAllByAuthorOrderByCreatedDatetimeDesc(authorId);
        return books.isEmpty() ? null : books.get(0);
    }


}
