package jungle.fairyTeller.fairyTale.dto;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class QuestionRequestDto implements Serializable {
    private String parameter1;
    private String parameter2;
    private String parameter3;
}
