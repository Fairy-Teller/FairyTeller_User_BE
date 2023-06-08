package jungle.fairyTeller.book.repository;

import jungle.fairyTeller.book.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer> {
    @Override
    List<BookEntity> findAll();
    List<BookEntity> findAllByAuthor(Integer authorId);


}
