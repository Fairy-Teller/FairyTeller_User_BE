package jungle.fairyTeller.fairyTale.book.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class PageDTO {
    private Integer pageNo;
    private String fullStory;
    private String thumbnailUrl;
    private String audioUrl;
}
