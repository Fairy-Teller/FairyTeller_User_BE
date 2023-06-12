package jungle.fairyTeller.fairyTale.story.dto;

import lombok.Getter;

import java.io.Serializable;
@Getter
public class SummarizingRequestDto implements Serializable {
    private String text;
}
