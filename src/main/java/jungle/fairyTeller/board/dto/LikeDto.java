package jungle.fairyTeller.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {
    private String error;
    private Integer likeCount;
    private boolean liked;
    public LikeDto(Integer likeCount, boolean liked) {
        this.likeCount = likeCount;
        this.liked = liked;
    }
}