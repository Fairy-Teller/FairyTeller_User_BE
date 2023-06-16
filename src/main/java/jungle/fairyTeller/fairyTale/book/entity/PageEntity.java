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

    @MapsId("bookId")
    @ManyToOne
    @JoinColumn(name = "BOOK_ID")
    private BookEntity book;

    private String fullStory;

    private String imageUrl;

    private String audioUrl;

    public void setBook(BookEntity book) {
        this.book = book;

        if(!book.getPages().contains(this)) {
            book.getPages().add(this);
        }
    }

}


