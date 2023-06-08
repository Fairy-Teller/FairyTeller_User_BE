package jungle.fairyTeller.book.dto;

import jungle.fairyTeller.book.entity.BookEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private Integer bookId;
    private Integer author;
    private String title;
    private String thumbnailUrl;

    public BookDTO(final BookEntity entity) {
        this.bookId = entity.getBookId();
        this.author = entity.getAuthor();
        this.title = entity.getTitle();
        this.thumbnailUrl = entity.getThumbnailUrl();
    }

    public static BookEntity toEntity(final BookDTO dto) {
        return BookEntity.builder()
                .bookId(dto.getBookId())
                .author(dto.getAuthor())
                .title(dto.getTitle())
                .thumbnailUrl(dto.getThumbnailUrl())
                .build();
    }
}
