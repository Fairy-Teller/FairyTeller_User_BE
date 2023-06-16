package jungle.fairyTeller.fairyTale.book.dto;

import jungle.fairyTeller.fairyTale.book.entity.PageEntity;
import jungle.fairyTeller.fairyTale.book.entity.PageId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO {
    private int no;
    private String story;
    private String imageUrl;
    private String audioUrl;

    public PageDTO(final PageEntity entity) {
        this.no = entity.getPageNo().getPageNo();
        this.story = entity.getFullStory();
        this.imageUrl = entity.getImageUrl();
        this.audioUrl = entity.getAudioUrl();
    }

    public static PageEntity toEntity(final PageDTO dto) {
        PageEntity pageEntity = PageEntity.builder()
                .pageNo(new PageId(dto.getNo(), null))
                .fullStory(dto.getStory())
                .imageUrl(dto.getImageUrl())
                .audioUrl(dto.getAudioUrl())
                .build();
        // Set the book entity if needed
        // pageEntity.setBook(bookEntity);
        return pageEntity;
    }
}
