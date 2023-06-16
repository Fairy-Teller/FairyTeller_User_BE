package jungle.fairyTeller.fairyTale.book.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

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

    @CreationTimestamp
    private Date createdDatetime; // LocalDateTime

}