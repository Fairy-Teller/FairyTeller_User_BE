package jungle.fairyTeller.fairyTale.story.dto.chatGpt;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
@Setter
@Getter
public class QuestionRequestDto implements Serializable {
    private String parameter1;
    private String parameter2;
    private String parameter3;
    private String parameter4;
    private String parameter5;
}
