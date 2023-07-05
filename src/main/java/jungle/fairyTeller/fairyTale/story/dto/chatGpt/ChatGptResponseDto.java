package jungle.fairyTeller.fairyTale.story.dto.chatGpt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) //필요없는 필드는 무시하도록 설정
public class ChatGptResponseDto implements Serializable {

    @JsonProperty("text")
    private String text;

    @JsonCreator
    public ChatGptResponseDto(@JsonProperty("choices") List<Choice> choices) {
        if (choices != null && !choices.isEmpty()) {
            this.text = choices.get(0).getText();
        }
    }

//    @JsonCreator
//    public ChatGptResponseDto(@JsonProperty("choices") List<Choice> choices) {
//        if (choices != null && !choices.isEmpty()) {
//            this.text = choices.get(0).getMessage().getContent();
//        }
//    }
}
