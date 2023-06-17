package jungle.fairyTeller.board.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Integer boardId;
    private Integer commentId;
    private Integer userId;
    private String nickname;
    private String content;
    //private boolean editable;
    private Date createdDatetime;
}
