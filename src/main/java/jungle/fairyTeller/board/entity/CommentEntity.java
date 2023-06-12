package jungle.fairyTeller.board.entity;

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
@Table(name = "comment")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer commentId;

    @Column(nullable = false)
    private Integer boardId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String author;
    @Column(nullable = false)
    private Integer userId;

    @CreationTimestamp
    private Date createdDatetime;
}
