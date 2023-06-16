package jungle.fairyTeller.fairyTale.book.entity;

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

    @OneToMany(mappedBy = "book")
    private List<PageEntity> pages = new ArrayList<PageEntity>();

    @CreationTimestamp
    private Date createdDatetime; // LocalDateTime

}