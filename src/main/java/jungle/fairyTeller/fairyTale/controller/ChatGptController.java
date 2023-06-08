package jungle.fairyTeller.fairyTale.controller;

import jungle.fairyTeller.fairyTale.dto.ChatGptResponseDto;
import jungle.fairyTeller.fairyTale.dto.QuestionRequestDto;
import jungle.fairyTeller.fairyTale.service.ChatGptService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat-gpt")
public class ChatGptController {
    private final ChatGptService chatGptService;

    public ChatGptController(ChatGptService chatGptService){
        this.chatGptService = chatGptService;
    }
    @PostMapping("/question")
    public ChatGptResponseDto sendQuestion(@RequestBody QuestionRequestDto requestDto){
        return chatGptService.askQuestion(requestDto);
    }
}
