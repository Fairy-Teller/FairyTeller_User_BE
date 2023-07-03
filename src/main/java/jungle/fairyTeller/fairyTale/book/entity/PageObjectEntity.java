package jungle.fairyTeller.fairyTale.book.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "pageObjects")
@NoArgsConstructor
@AllArgsConstructor
public class PageObjectEntity implements Serializable {

    @Id
    private PageId id;

    private Object objects;

    @CreationTimestamp
    private Date createdAt; // LocalDateTime
}
