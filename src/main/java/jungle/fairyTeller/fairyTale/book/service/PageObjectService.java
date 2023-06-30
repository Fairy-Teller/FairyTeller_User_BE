package jungle.fairyTeller.fairyTale.book.service;

import jungle.fairyTeller.fairyTale.book.entity.PageId;
import jungle.fairyTeller.fairyTale.book.entity.PageObjectEntity;
import jungle.fairyTeller.fairyTale.book.repository.PageObjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class PageObjectService {

    @Autowired
    private PageObjectRepository pageObjectRepository;

    public void saveObjects(PageObjectEntity pageObjectEntity) {
        pageObjectRepository.save(pageObjectEntity);

        PageId pageId = pageObjectEntity.getId();

        log.info("Saved or updated PageObjectEntity with PageId {}", pageId.toString());
    }

    public List<PageObjectEntity> findById(PageId id) {
        return pageObjectRepository.findById(id);
    }
}
