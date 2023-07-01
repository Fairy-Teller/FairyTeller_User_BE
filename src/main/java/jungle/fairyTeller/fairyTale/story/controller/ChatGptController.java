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

import java.util.ArrayList;
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
//    @PostMapping("/question")
//    public ResponseEntity<String> sendQuestion
//            (@RequestBody QuestionRequestDto requestDto,@AuthenticationPrincipal String userId){
//       return chatGptService.askQuestion(chatGptService.koreanEnglishMapping(requestDto),1);
//    }

    @PostMapping("/question")
    public  ResponseEntity<List<HashMap<String, Object>>> sendQuestion(@RequestBody QuestionRequestDto requestDto){
        return chatGptService.askQuestion(chatGptService.koreanEnglishMapping(requestDto),1);
    }

//    @PostMapping("/question/recreate")
//    public ResponseEntity<List<HashMap<String, Object>>> recreateQuestion
//            (@RequestBody QuestionRequestDto requestDto,@AuthenticationPrincipal String userId){
//        return chatGptService.askQuestion(chatGptService.koreanEnglishMapping(requestDto),2);
//    }

    @PostMapping("/textToImage")
    public ResponseEntity<Object> textToImage(@RequestBody SummarizingRequestDto requestDto,
                                                          @AuthenticationPrincipal String userId){
        try {
            if(requestDto == null || requestDto.getText() == null) {
                throw new RuntimeException("requestDto is null.");
            }
            String transToText =translationService.translate(requestDto.getText(),"ko","en");
            //요약 로직
           // requestDto.setText(transToText);
          //  ChatGptResponseDto gptResponseDto = chatGptService.askSummarize(requestDto);
          //  String summaryText = gptResponseDto.getText();
          //  summaryText = summaryText.replace("\n\n","");
          //  System.out.println("확인용:"+summaryText);
            transToText = createImgService.addLora(1, transToText);
            String base64Image = createImgService.createImg(transToText);

            HttpHeaders headers = new HttpHeaders();
            // headers.setContentType(MediaType.valueOf("image/jpeg"));

            // byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            return new ResponseEntity<>(base64Image, headers, HttpStatus.OK);
        }catch (Exception e){
            log.error("Failed to create image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/textToImage/v2")
    public ResponseEntity<Object> textToImageWithLora(@RequestBody SummarizingRequestDto requestDto,
                                                      @AuthenticationPrincipal String userId){
        try {
            if(requestDto == null || requestDto.getText() == null) {
                throw new RuntimeException("requestDto is null.");
            }
            String transToText =translationService.translate(requestDto.getText(),"ko","en");
            transToText = createImgService.addLora(requestDto.getLoraNo(), transToText);
            String base64Image = createImgService.createImg(transToText);

            HttpHeaders headers = new HttpHeaders();
            return new ResponseEntity<>(base64Image, headers, HttpStatus.OK);
        }catch (Exception e){
            log.error("Failed to create image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/textToImage/test")
    public ResponseEntity<Object> textToImageWithLoraTest(@RequestBody SummarizingRequestDto requestDto,
                                                      @AuthenticationPrincipal String userId){
        try {
            if(requestDto == null || requestDto.getText() == null) {
                throw new RuntimeException("requestDto is null.");
            }
            String base64Image = createImgService.createCatImg();

            HttpHeaders headers = new HttpHeaders();
            return new ResponseEntity<>(base64Image, headers, HttpStatus.OK);
        }catch (Exception e){
            log.error("Failed to create image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/question/test/4")
    public ResponseEntity<List<HashMap<String, Object>>> testQuestion1
            (@RequestBody QuestionRequestDto requestDto,@AuthenticationPrincipal String userId){
        List<HashMap<String,Object>> responseList = new ArrayList<>();

        for(int i=0;i<4;i++){
            HashMap<String, Object> resultMap = new HashMap<>();
            resultMap.put("paragraph",i);
            responseList.add(resultMap);
        }

        return ResponseEntity.ok(responseList);
    }

    @PostMapping("/question/test/5")
    public ResponseEntity<List<HashMap<String, Object>>> testQuestion2
            (@RequestBody QuestionRequestDto requestDto,@AuthenticationPrincipal String userId){
        List<HashMap<String,Object>> responseList = new ArrayList<>();

        for(int i=0;i<5;i++){
            HashMap<String, Object> resultMap = new HashMap<>();
            resultMap.put("paragraph",i);
            responseList.add(resultMap);
        }

        return ResponseEntity.ok(responseList);
    }

    @PostMapping("/question/test/6")
    public ResponseEntity<List<HashMap<String, Object>>> testQuestion3
            (@RequestBody QuestionRequestDto requestDto,@AuthenticationPrincipal String userId){
        List<HashMap<String,Object>> responseList = new ArrayList<>();

        for(int i=0;i<6;i++){
            HashMap<String, Object> resultMap = new HashMap<>();
            resultMap.put("paragraph",i);
            responseList.add(resultMap);
        }

        return ResponseEntity.ok(responseList);
    }
}
