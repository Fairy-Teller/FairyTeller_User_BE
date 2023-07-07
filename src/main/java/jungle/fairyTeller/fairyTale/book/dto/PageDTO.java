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
    private Integer pageNo;
    private String fullStory;

    private String originalImageUrl;

    private String finalImageUrl;
    private String audioUrl;
    private String userAudioUrl;
    private int imageCreationRestrictionsNumber;
    private boolean isDark;

    private Object objects;

    public PageDTO(final PageEntity entity) {
        this.pageNo = entity.getPageNo().getPageNo();
        this.fullStory = entity.getFullStory();
        this.originalImageUrl = entity.getOriginalImageUrl();
        this.finalImageUrl = entity.getFinalImageUrl();
        this.audioUrl = entity.getAudioUrl();
        this.userAudioUrl = entity.getUserAudioUrl();
        this.isDark = true;
    }

    public static PageEntity toEntity(PageDTO dto) {
        PageEntity entity = PageEntity.builder()
                .pageNo(new PageId(dto.getPageNo(), null))
                .fullStory(dto.getFullStory())
                .originalImageUrl(dto.getOriginalImageUrl())
                .finalImageUrl(dto.getFinalImageUrl())
                .audioUrl(dto.getAudioUrl())
                .userAudioUrl(dto.getUserAudioUrl())
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
                .originalImageUrl(entity.getOriginalImageUrl())
                .finalImageUrl(entity.getFinalImageUrl())
                .audioUrl(entity.getAudioUrl())
                .build();
    }

}
