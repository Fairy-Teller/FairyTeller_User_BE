package jungle.fairyTeller.board.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "board")
public class BoardEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer boardId;
    @Column(nullable = false)
    private Integer bookId;
    @Column(nullable = false)
    private Integer authorId;
    private String nickname;
    @Column(nullable = false)
    private String title;
    private String content;
    private String thumbnailUrl;
    @CreationTimestamp
    private LocalDateTime createdDatetime; // LocalDateTime
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> comments;
}
