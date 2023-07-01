package jungle.fairyTeller.fairyTale.story.dto.chatGpt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class Choice implements Serializable {

    private Message message;
        public Choice(Message message) {
        this.message = message;
    }

//    private String text;
//    private Integer index;
//    @JsonProperty("finish_reason")
//    private String finishReason;

//    public Choice(String text, Integer index, String finishReason,Message message) {
//        this.text = text;
//        this.index = index;
//        this.finishReason = finishReason;
//        this.message = message;
//    }
}
