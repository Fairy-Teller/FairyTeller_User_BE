package jungle.fairyTeller.fairyTale.story.controller;

import jungle.fairyTeller.fairyTale.story.dto.ChatGptResponseDto;
import jungle.fairyTeller.fairyTale.story.dto.QuestionRequestDto;
import jungle.fairyTeller.fairyTale.story.dto.SummarizingRequestDto;
import jungle.fairyTeller.fairyTale.story.service.ChatGptService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat-gpt")
public class ChatGptController {
    private final ChatGptService chatGptService;

    public ChatGptController(ChatGptService chatGptService){
        this.chatGptService = chatGptService;
    }
    @PostMapping("/question")
    public ChatGptResponseDto sendQuestion(@RequestBody QuestionRequestDto requestDto,
                                           @AuthenticationPrincipal String userId){
        return chatGptService.askQuestion(requestDto);
    }

    @PostMapping("/summarize")
    public void sendQuestionToSummarize(@RequestBody SummarizingRequestDto requestDto,
                                                       @AuthenticationPrincipal String userId){
        ChatGptResponseDto gptResponseDto = chatGptService.askSummarize(requestDto);
        String summaryText = gptResponseDto.getText();
        summaryText = summaryText.replace("\n\n","");
        System.out.println("확인용:"+summaryText);
        //이미지 생성 AI 메소드 호출해야함:!!!

    }
}
