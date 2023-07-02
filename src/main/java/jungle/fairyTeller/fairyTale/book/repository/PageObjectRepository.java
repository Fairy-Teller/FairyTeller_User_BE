package jungle.fairyTeller.fairyTale.book.repository;

import jungle.fairyTeller.fairyTale.book.entity.PageId;
import jungle.fairyTeller.fairyTale.book.entity.PageObjectEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PageObjectRepository extends MongoRepository<PageObjectEntity, String> {

    List<PageObjectEntity> findById(PageId id);
    boolean existsById(PageId id);
}
