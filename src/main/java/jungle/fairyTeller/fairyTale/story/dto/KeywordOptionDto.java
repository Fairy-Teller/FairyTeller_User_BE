package jungle.fairyTeller.fairyTale.story.dto;

import jungle.fairyTeller.fairyTale.story.entity.KeywordEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class KeywordOptionDto implements Serializable {
    private KeywordEnum keywordEnum;
    private String title;

    public KeywordOptionDto(KeywordEnum keywordEnum, String title){
        this.keywordEnum = keywordEnum;
        this.title = title;
    }
}

