package jungle.fairyTeller.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Integer commentId;
    private Integer boardId;
    private Integer userId;
    private String nickname;
    private String content;
    private LocalDateTime createdDatetime;
}
