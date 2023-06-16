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
    private String nickname;
    private String title;
    private String content;
    private String thumbnailUrl;
    private String audioUrl;
    private List<CommentDto> comments;
    private boolean editable; // New property for indicating editability

    public BoardDto(final BoardEntity entity) {
        this.boardId = entity.getBoardId();
        this.bookId = entity.getBookId();
        this.author = entity.getAuthor();
        this.nickname = entity.getNickname();
        this.title = entity.getTitle();
        this.thumbnailUrl = entity.getThumbnailUrl();
        this.audioUrl = entity.getAudioUrl();
        this.content = entity.getContent();
        this.comments = new ArrayList<>();
    }

    public static BoardEntity toEntity(final BoardDto dto) {
        return BoardEntity.builder()
                .boardId(dto.getBoardId())
                .bookId(dto.getBookId())
                .author(dto.getAuthor())
                .nickname(dto.getNickname())
                .title(dto.getTitle())
                .content(dto.getContent())
                .thumbnailUrl(dto.getThumbnailUrl())
                .audioUrl(dto.getAudioUrl())
                .build();
    }
}
