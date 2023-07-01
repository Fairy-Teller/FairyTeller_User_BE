package jungle.fairyTeller.fairyTale.book.service;

import com.sun.source.tree.LambdaExpressionTree;
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

    // 줄거리 확정 이후 BookId를 채번하고, 줄거리만을 저장하는 로직
    // 다중 페이지 형식 이후 수정
    public BookEntity createBookId(final BookEntity entity) {
        validate(entity);

        bookRepository.save(entity);

        log.info("Book Entity Id : {} is saved", entity.getBookId());

        return bookRepository.findByBookId(entity.getBookId());
    }

    public BookEntity updateTheme(final BookEntity entity) {
        validate(entity);

        bookRepository.save(entity);

        log.info("Book Entity Id : {} theme is saved", entity.getBookId());

        return bookRepository.findByBookId(entity.getBookId());
    }

    // 존재하는 BookId에 동화 최종제목, 이미지, audio update 하는 로직
    public BookEntity updateTitleStoryAudio(final BookEntity entity) {
        validate(entity);

        bookRepository.save(entity);

        log.info("Book Entity Id : {} is updated", entity.getBookId());

        return bookRepository.findByBookId(entity.getBookId());
    }

    public BookEntity updateUserVoice(final BookEntity entity) {
        validate(entity);

        bookRepository.save(entity);

        log.info("Book Entity Id : {} is updated", entity.getBookId());

        return bookRepository.findByBookId(entity.getBookId());
    }

    // userId로 조회
    public List<BookEntity> retrieve(final Integer userId) {
        return bookRepository.findAllByAuthor(userId);
    }

    public BookEntity retrieveLatestByUserId(final Integer userId) {
        return bookRepository.findLatestByAuthor(userId);
    }

    public BookEntity retrieveByBookId(final Integer bookId) {
        return bookRepository.findByBookId(bookId);
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
    public BookEntity getBookByBookId(Integer bookId){
        return bookRepository.findByBookId(bookId);
    }

    public int countByAuthorAndEditFinal(Integer authorId){
        return bookRepository.countByAuthorAndEditFinal(authorId);
    }

    public List<BookEntity> getLatestBookByAuthor(Integer authorId) {
        return bookRepository.findByAuthorAndEditFinalOrderByLastModifiedDateDesc(authorId, false);
    }
}
