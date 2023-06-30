package jungle.fairyTeller.fairyTale.book.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jungle.fairyTeller.board.entity.BoardEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "books")
public class BookEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer bookId;

    @Column(nullable = false)
    private Integer author;

    private String title;

    private String thumbnailUrl;

    private Integer theme;

    private boolean imageFinal = false;

    private boolean editFinal = false;

    @JsonIgnore
    @OneToMany(mappedBy = "book")
    private List<PageEntity> pages = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "book")
    private List<BoardEntity> boards = new ArrayList<>();

    @CreationTimestamp
    private Date createdDatetime; // LocalDateTime

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    public boolean isImageFinal() {
        return imageFinal;
    }
    public boolean isEditFinal() {
        return editFinal;
    }
}