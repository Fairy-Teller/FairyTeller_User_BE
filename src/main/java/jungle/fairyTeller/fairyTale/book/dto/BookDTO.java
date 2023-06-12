package jungle.fairyTeller.fairyTale.book.dto;

import jungle.fairyTeller.fairyTale.book.entity.BookEntity;
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
    private String fullStory;
    private String thumbnailUrl;

    private String audioUrl;

    public BookDTO(final BookEntity entity) {
        this.bookId = entity.getBookId();
        this.author = entity.getAuthor();
        this.title = entity.getTitle();
        this.fullStory = entity.getFullStory();
        this.thumbnailUrl = entity.getThumbnailUrl();
        this.audioUrl = entity.getAudioUrl();
    }

    public static BookEntity toEntity(final BookDTO dto) {
        return BookEntity.builder()
                .bookId(dto.getBookId())
                .author(dto.getAuthor())
                .title(dto.getTitle())
                .fullStory(dto.getFullStory())
                .thumbnailUrl(dto.getThumbnailUrl())
                .audioUrl(dto.getAudioUrl())
                .build();
    }
}
