package jungle.fairyTeller.fairyTale.story.controller;

import jungle.fairyTeller.fairyTale.Image.service.CreateImgService;
import jungle.fairyTeller.fairyTale.story.dto.chatGpt.ChatGptResponseDto;
import jungle.fairyTeller.fairyTale.story.dto.chatGpt.QuestionRequestDto;
import jungle.fairyTeller.fairyTale.story.dto.SummarizingRequestDto;
import jungle.fairyTeller.fairyTale.story.service.ChatGptService;
import jungle.fairyTeller.fairyTale.story.service.PaPagoTranslationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/chat-gpt")
public class ChatGptController {
    private final ChatGptService chatGptService;
    private final CreateImgService createImgService;
    private final PaPagoTranslationService translationService;

    public ChatGptController(ChatGptService chatGptService, CreateImgService createImgService,
                             PaPagoTranslationService translationService){
        this.chatGptService = chatGptService;
        this.createImgService = createImgService;
        this.translationService = translationService;
    }
    @PostMapping("/question")
    public HttpEntity<List<HashMap<String, Object>>> sendQuestion
            (@RequestBody QuestionRequestDto requestDto,@AuthenticationPrincipal String userId){

        return chatGptService.askQuestion(requestDto);
    }

    @PostMapping("/summarize")
    public ResponseEntity<Object> sendQuestionToSummarize(@RequestBody SummarizingRequestDto requestDto,
                                                          @AuthenticationPrincipal String userId){
        try {
            if(requestDto == null || requestDto.getText() == null) {
                throw new RuntimeException("requestDto is null.");
            }
            String transToText =translationService.translate(requestDto.getText(),"ko","en");
            requestDto.setText(transToText);
            ChatGptResponseDto gptResponseDto = chatGptService.askSummarize(requestDto);
            String summaryText = gptResponseDto.getText();
            summaryText = summaryText.replace("\n\n","");
            System.out.println("확인용:"+summaryText);
//
            String base64Image = createImgService.createImg("<lora:model1:1> " + summaryText);

            HttpHeaders headers = new HttpHeaders();
            // headers.setContentType(MediaType.valueOf("image/jpeg"));

            // byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            return new ResponseEntity<>(base64Image, headers, HttpStatus.OK);
        }catch (Exception e){
            log.error("Failed to create image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
