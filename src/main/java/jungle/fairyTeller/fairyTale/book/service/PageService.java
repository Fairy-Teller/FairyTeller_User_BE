package jungle.fairyTeller.fairyTale.book.service;

import jungle.fairyTeller.fairyTale.book.entity.PageEntity;
import jungle.fairyTeller.fairyTale.book.entity.PageId;
import jungle.fairyTeller.fairyTale.book.repository.PageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PageService {

    @Autowired
    private PageRepository pageRepository;

    public PageEntity retrieveByBookId(Integer bookId, Integer pageNo) {
        return pageRepository.findByPageNo(new PageId(bookId, pageNo));
    }

    public PageEntity updateUserAudio(final PageEntity entity) {
        validate(entity);

        pageRepository.save(entity);

        log.info("Page Entity : {} - {} is updated", entity.getBook().getBookId(), entity.getPageNo().getPageNo());

        return pageRepository.findByPageNo(new PageId(entity.getBook().getBookId(), entity.getPageNo().getPageNo()));

    }

    private void validate(final PageEntity entity) {
        if(entity == null) {
            log.warn("Entity cannot be null");
            throw new RuntimeException("Entity cannot be null");
        }

        if(entity.getBook() == null) {
            log.warn("Unknown Book");
            throw new RuntimeException("Unknown Book");
        }
    }
}
