package jungle.fairyTeller.board.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer commentId;

    @MapsId("boardId")
    @ManyToOne
    @JoinColumn(name = "boardId")
    private BoardEntity board;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String content;

    @CreationTimestamp
    private LocalDateTime createdDatetime;
}
