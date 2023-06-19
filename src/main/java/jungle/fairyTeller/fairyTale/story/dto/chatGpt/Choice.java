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
    private String text;

    @Builder
    @JsonCreator
    public Choice(@JsonProperty("text") String text) {
        this.text = text;
    }
}
