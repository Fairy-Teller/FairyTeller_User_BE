package jungle.fairyTeller.fairyTale.story.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
public class SummarizingRequestDto implements Serializable {
    private int bookId;
    private int pageNo;
    private int loraNo;
    private String text;
}
