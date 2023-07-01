package jungle.fairyTeller.fairyTale.story.dto.chatGpt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;


//@Getter
//@Setter
//@NoArgsConstructor
//public class ChatGptResponseDto implements Serializable {
//    @JsonProperty("text")
//    private String text;
//
//    @JsonCreator
//    public ChatGptResponseDto(@JsonProperty("choices") List<Choice> choices) {
//        if (choices != null && !choices.isEmpty()) {
//            this.text = choices.get(0).getText();
//        }
//    }
//
//}
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) //필요없는 필드는 무시하도록 설정
public class ChatGptResponseDto implements Serializable {

//    private List<Choice> choices;
//    @Builder
//    public ChatGptResponseDto(List<Choice> choices) {
//        this.choices = choices;
//    }

    @JsonProperty("text")
    private String text;

    @JsonCreator
    public ChatGptResponseDto(@JsonProperty("choices") List<Choice> choices) {
        if (choices != null && !choices.isEmpty()) {
            this.text = choices.get(0).getMessage().getContent();
        }
    }

//    private String id;
//    private String object;
//   private LocalDate created;
//    private String model;
//    @Builder
//    public ChatGptResponseDto(String id, String object,
//                              LocalDate created, String model,
//                              List<Choice> choices) {
//        this.id = id;
//        this.object = object;
//        this.created = created;
//        this.model = model;
//        this.choices = choices;
//    }
}