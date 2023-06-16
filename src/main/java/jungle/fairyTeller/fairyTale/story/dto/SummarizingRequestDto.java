package jungle.fairyTeller.fairyTale.story.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
public class SummarizingRequestDto implements Serializable {
    private String text;
}
