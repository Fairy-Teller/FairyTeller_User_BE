package jungle.fairyTeller.fairyTale.book.service;

import jungle.fairyTeller.fairyTale.book.entity.BookEntity;
import jungle.fairyTeller.fairyTale.book.entity.PageEntity;
import jungle.fairyTeller.fairyTale.book.entity.PageId;
import jungle.fairyTeller.fairyTale.book.repository.BookRepository;
import jungle.fairyTeller.fairyTale.book.repository.PageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PageService {

    @Autowired
    private PageRepository pageRepository;

    public PageEntity retrieveByPageId(PageId pageId) {
        return pageRepository.findByPageNo(pageId);
    }

    public List<PageEntity> retrieveByBookId(Integer bookId) {
        return pageRepository.findAllByBookBookId(bookId);
    }

    // 페이지가 생성되면서 줄거리가 저장된다
    public PageEntity createPage(final PageEntity entity) {
        validate(entity);
        pageRepository.save(entity);
        log.info("Page Entity : {} - {} is saved", entity.getBook().getBookId(), entity.getPageNo().getPageNo());

        return pageRepository.findByPageNo(entity.getPageNo());
    }

    public PageEntity updatePage(final PageEntity entity) {
        validate(entity);

        pageRepository.save(entity);

        log.info("Page Entity : {} - {} is updated", entity.getBook().getBookId(), entity.getPageNo().getPageNo());

        return pageRepository.findByPageNo(entity.getPageNo());
    }

    private void validate(final PageEntity entity) {
        if(entity == null) {
            log.warn("Entity cannot be null");
            throw new RuntimeException("Entity cannot be null");
        }
    }
    public void deletePagesByBookId(Integer bookId) {
        pageRepository.deleteByBookId(bookId);
    }
}
