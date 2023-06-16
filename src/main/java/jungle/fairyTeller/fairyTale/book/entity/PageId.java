package jungle.fairyTeller.fairyTale.book.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageId implements Serializable {

    private Integer bookId;
    private Integer pageNo;
}
