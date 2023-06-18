package jungle.fairyTeller.board.dto;
import jungle.fairyTeller.board.entity.CommentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    private boolean editable;
    private Date createdDatetime;

    public static List<CommentDto> fromEntityList(List<CommentEntity> commentEntities) {
        if (commentEntities == null) {
            return Collections.emptyList();
        }
        return commentEntities.stream()
                .map(CommentDto::fromEntity)
                .collect(Collectors.toList());
    }

    public static CommentDto fromEntity(CommentEntity commentEntity) {
        return CommentDto.builder()
                .boardId(commentEntity.getBoard().getBoardId())
                .commentId(commentEntity.getCommentId())
                .userId(commentEntity.getUser().getId())
                .nickname(commentEntity.getUser().getNickname())
                .content(commentEntity.getContent())
                .createdDatetime(commentEntity.getCreatedDatetime())
                .build();
    }

}
