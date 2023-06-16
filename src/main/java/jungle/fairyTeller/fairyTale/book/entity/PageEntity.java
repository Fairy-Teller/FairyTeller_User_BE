package jungle.fairyTeller.fairyTale.book.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pages")
public class PageEntity {

    @EmbeddedId
    private PageId pageNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookId", insertable = false, updatable = false)
    private BookEntity book;

    private String fullStory;

    private String thumbnailUrl;

    private String audioUrl;

}


