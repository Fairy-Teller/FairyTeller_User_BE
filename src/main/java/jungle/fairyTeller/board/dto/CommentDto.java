package jungle.fairyTeller.board.dto;
import jungle.fairyTeller.board.entity.CommentEntity;
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
    private Integer commentId;
    private Integer boardId;
    private String content;
    private String author;
    private Integer userId;
    private Date createdDatetime;

    public CommentDto(CommentEntity entity) {
        this.commentId = entity.getCommentId();
        this.boardId = entity.getBoardId();
        this.content = entity.getContent();
        this.author = entity.getAuthor();
        this.userId = entity.getUserId();
        this.createdDatetime = entity.getCreatedDatetime();
    }

    public static CommentEntity toEntity(CommentDto dto) {
        return CommentEntity.builder()
                .commentId(dto.getCommentId())
                .boardId(dto.getBoardId())
                .content(dto.getContent())
                .author(dto.getAuthor())
                .userId(dto.getUserId())
                .createdDatetime(dto.getCreatedDatetime())
                .build();
    }
}
