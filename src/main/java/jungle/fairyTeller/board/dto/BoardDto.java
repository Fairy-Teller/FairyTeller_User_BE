package jungle.fairyTeller.board.dto;

import jungle.fairyTeller.board.entity.BoardEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {
    private Integer boardId;
    private Integer bookId;
    private Integer author;
    private String title;
    private String content;
    private String thumbnailUrl;
    private List<CommentDto> comments;
    public BoardDto(final BoardEntity entity) {
        this.boardId = entity.getBoardId();
        this.bookId = entity.getBookId();
        this.author = entity.getAuthor();
        this.title = entity.getTitle();
        this.thumbnailUrl = entity.getThumbnailUrl();
        this.content = entity.getContent();
        this.comments = new ArrayList<>();
    }
    public static BoardEntity toEntity(final BoardDto dto) {
        return BoardEntity.builder()
                .boardId(dto.getBoardId())
                .bookId(dto.getBookId())
                .author(dto.getAuthor())
                .title(dto.getTitle())
                .content(dto.getContent())
                .thumbnailUrl(dto.getThumbnailUrl())
                .build();
    }
}
