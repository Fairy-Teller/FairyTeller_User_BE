package jungle.fairyTeller.board.dto;

import jungle.fairyTeller.fairyTale.book.dto.PageDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardContentDto {
    private Integer boardId;
    private Integer bookId;
    private String title;
    private String thumbnailUrl;
    private Date createdDatetime;
    private Integer authorId;
    private String nickname;
    private Integer likeCount;
    private boolean liked;
    private boolean editable;
    private Integer viewCount;
}