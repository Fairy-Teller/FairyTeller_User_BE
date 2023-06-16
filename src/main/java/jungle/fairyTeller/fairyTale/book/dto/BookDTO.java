package jungle.fairyTeller.fairyTale.book.dto;

import jungle.fairyTeller.fairyTale.book.entity.BookEntity;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookDTO {
    private Integer bookId;
    private Integer author;
    private String title;
    private String thumbnailUrl;
    private List<PageDTO> pages;

    public BookDTO(final BookEntity entity) {
        this.bookId = entity.getBookId();
        this.author = entity.getAuthor();
        this.title = entity.getTitle();
        this.thumbnailUrl = entity.getThumbnailUrl();
        this.pages = entity.getPages()
                .stream()
                .map(PageDTO::new)
                .collect(Collectors.toList());
    }

    public static BookEntity toEntity(final BookDTO dto) {
        return BookEntity.builder()
                .bookId(dto.getBookId())
                .author(dto.getAuthor())
                .title(dto.getTitle())
                .thumbnailUrl(dto.getThumbnailUrl())
                .pages(dto.getPages()
                        .stream()
                        .map(PageDTO::toEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}
