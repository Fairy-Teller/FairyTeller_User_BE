package jungle.fairyTeller.fairyTale.story.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
public class ResultDTO implements Serializable {
    private String translatedText;

    @Builder
    public ResultDTO(String translatedText) {
        this.translatedText = translatedText;
    }
}