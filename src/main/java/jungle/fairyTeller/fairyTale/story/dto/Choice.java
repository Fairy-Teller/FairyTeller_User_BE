package jungle.fairyTeller.fairyTale.story.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class Choice implements Serializable {
    private String text;

    @Builder
    @JsonCreator
    public Choice(@JsonProperty("text") String text) {

        //gpt가 작성한 시나리오 문자열 parsing
        String tmpScenerio = text;
        tmpScenerio = tmpScenerio.replaceAll("\n\n","");
        tmpScenerio = tmpScenerio.replaceAll("\\.",".\n");
        this.text = tmpScenerio;
    }
}
