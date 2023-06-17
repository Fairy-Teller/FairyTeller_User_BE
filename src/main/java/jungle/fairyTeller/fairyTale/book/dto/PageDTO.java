package jungle.fairyTeller.fairyTale.book.dto;

import jungle.fairyTeller.fairyTale.book.entity.PageEntity;
import jungle.fairyTeller.fairyTale.book.entity.PageId;
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
public class PageDTO {
    private int pageNo;
    private String fullStory;
    private String imageUrl;
    private String audioUrl;

    public PageDTO(final PageEntity entity) {
        this.pageNo = entity.getPageNo().getPageNo();
        this.fullStory = entity.getFullStory();
        this.imageUrl = entity.getImageUrl();
        this.audioUrl = entity.getAudioUrl();
    }

    public static PageEntity toEntity(PageDTO dto) {
        PageEntity entity = PageEntity.builder()
                .pageNo(new PageId(dto.getPageNo(), null))
                .fullStory(dto.getFullStory())
                .imageUrl(dto.getImageUrl())
                .audioUrl(dto.getAudioUrl())
                .build();
        // 필요하다면 book과의 관계 설정
        return entity;
    }

    public static List<PageDTO> fromEntityList(List<PageEntity> entities) {
        List<PageDTO> dtos = new ArrayList<>();
        for (PageEntity entity : entities) {
            dtos.add(fromEntity(entity));
        }
        return dtos;
    }

    public static PageDTO fromEntity(PageEntity entity) {
        return PageDTO.builder()
                .pageNo(entity.getPageNo().getPageNo())
                .fullStory(entity.getFullStory())
                .imageUrl(entity.getImageUrl())
                .audioUrl(entity.getAudioUrl())
                .build();
    }

}
