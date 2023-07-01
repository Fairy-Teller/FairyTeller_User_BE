package jungle.fairyTeller.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {
    private Integer authorId;
    private String nickname;
    private int totalHeart;
}
