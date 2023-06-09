package jungle.fairyTeller.fairyTale.book.service;

import jungle.fairyTeller.fairyTale.book.entity.BookEntity;
import jungle.fairyTeller.fairyTale.book.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<BookEntity> create(final BookEntity entity) {
        validate(entity);

        bookRepository.save(entity);

        log.info("Book Entity Id : {} is saved", entity.getBookId());

        return bookRepository.findAllByAuthor(entity.getAuthor());
    }

    // userId로 조회
    public List<BookEntity> retrieve(final Integer userId) {
        return bookRepository.findAllByAuthor(userId);
    }

    private void validate(final BookEntity entity) {
        if(entity == null) {
            log.warn("Entity cannot be null");
            throw new RuntimeException("Entity cannot be null");
        }

        if(entity.getAuthor() == null) {
            log.warn("Unknown Author");
            throw new RuntimeException("Unknown Author");
        }
    }
}
