package jungle.fairyTeller.fairyTale.story.dto.chatGpt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String role;
    private String content;
}
