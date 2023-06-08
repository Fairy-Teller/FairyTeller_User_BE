package jungle.fairyTeller.fairyTale.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;


@Getter
@NoArgsConstructor
public class ChatGptResponseDto implements Serializable {
    @JsonProperty("text")
    private String text;

    @JsonCreator
    public ChatGptResponseDto(@JsonProperty("choices") List<Choice> choices) {
        if (choices != null && !choices.isEmpty()) {
            this.text = choices.get(0).getText();
        }
    }
}

/*
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) //필요없는 필드는 무시하도록 설정
public class ChatGptResponseDto implements Serializable {

//    private String id;
//    private String object;
//    private LocalDate created;
//    private String model;
//    private List<Choice> choices;

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
} */