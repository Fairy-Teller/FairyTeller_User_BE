package jungle.fairyTeller.board.entity;

import jungle.fairyTeller.fairyTale.book.entity.BookEntity;
import jungle.fairyTeller.fairyTale.book.entity.PageEntity;
import jungle.fairyTeller.user.entity.UserEntity;
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
@Table(name = "boards")
public class BoardEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer boardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookId")
    private BookEntity book;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorId")
    private UserEntity author;
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> comments = new ArrayList<>();

    private String title;
    @Column(nullable = true)
    private String description;
    private String thumbnailUrl;
    @CreationTimestamp
    private Date createdDatetime; // LocalDateTime
    public BoardEntity(String title, String thumbnailUrl, String description, BookEntity book, UserEntity author, List<PageEntity> pages) {
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
        this.book = book;
        this.author = author;
    }
}