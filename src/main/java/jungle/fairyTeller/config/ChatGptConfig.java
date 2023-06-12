package jungle.fairyTeller.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ChatGptConfig {
    @Value("${chatgpt.authorization}")
    private String authorization;

    @Value("${chatgpt.bearer}")
    private String bearer;

    @Value("${chatgpt.api-key}")
    private String apiKey;

    @Value("${chatgpt.model}")
    private String model;

    @Value("${chatgpt.max-token}")
    private Integer maxToken;

    @Value("${chatgpt.temperature}")
    private Double temperature;

    @Value("${chatgpt.top-p}")
    private Double topP;

    @Value("${chatgpt.media-type}")
    private String mediaType;

    @Value("${chatgpt.url}")
    private String url;

}


