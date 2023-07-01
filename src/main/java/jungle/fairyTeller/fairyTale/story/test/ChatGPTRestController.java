package jungle.fairyTeller.fairyTale.story.test;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatGPTRestController {
    private final GPTChatRestService gptChatRestService;

    @PostMapping("/completion")
    public CompletionResponse completion(final @RequestBody GPTCompletionRequest gptCompletionRequest) {

        return gptChatRestService.completion(gptCompletionRequest);
    }
    @PostMapping("/completion/chat")
    public CompletionChatResponse completionChat(final @RequestBody GPTCompletionChatRequest gptCompletionChatRequest) {

        return gptChatRestService.completionChat(gptCompletionChatRequest);
    }
}
