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
@Table(name = "board")
public class BoardEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer boardId;
    @Column(nullable = false)
    private Integer bookId;
    @Column(nullable = false)
    private Integer author;
    private String nickname;
    @Column(nullable = false)
    private String title;
    private String content;
    private String thumbnailUrl;
    private String audioUrl;
    @CreationTimestamp
    private Date createdDatetime; // LocalDateTime
}