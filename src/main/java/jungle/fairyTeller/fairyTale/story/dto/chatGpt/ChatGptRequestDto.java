package jungle.fairyTeller.fairyTale.story.dto.chatGpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ChatGptRequestDto implements Serializable {

    private String model;
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    private Double temperature;
    @JsonProperty("top_p")
    private Double topP;

    private List<Map<String,String>> messages;

    @Builder
    public ChatGptRequestDto(String model,List<Map<String,String>> messages,
                             Integer maxTokens, Double temperature,
                             Double topP) {
        this.model = model;
        this.messages = messages;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
        this.topP = topP;
    }
}