package jungle.fairyTeller.board.dto;

import jungle.fairyTeller.board.entity.BoardEntity;
import jungle.fairyTeller.fairyTale.book.dto.BookDTO;
import jungle.fairyTeller.fairyTale.book.dto.PageDTO;
import jungle.fairyTeller.board.entity.CommentEntity;
import jungle.fairyTeller.fairyTale.book.entity.BookEntity;
import jungle.fairyTeller.fairyTale.book.entity.PageEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {
    private Integer boardId;
    private Integer bookId;
    private String title;
    private String description;
    private String thumbnailUrl;
    private Date createdDatetime;
    private List<PageDTO> pages;
    private List<CommentDto> comments;
    private Integer authorId;
    private String nickname;

}
