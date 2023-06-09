package jungle.fairyTeller.fairyTale.story.service;

import jungle.fairyTeller.fairyTale.story.dto.ChatGptRequestDto;
import jungle.fairyTeller.fairyTale.story.dto.ChatGptResponseDto;
import jungle.fairyTeller.fairyTale.story.dto.QuestionRequestDto;
import jungle.fairyTeller.config.ChatGptConfig;
import jungle.fairyTeller.fairyTale.story.dto.SummarizingRequestDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatGptService {

    private static RestTemplate restTemplate = new RestTemplate();

    public HttpEntity<ChatGptRequestDto> buildHttpEntity(ChatGptRequestDto requestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(ChatGptConfig.MEDIA_TYPE));
        headers.add(ChatGptConfig.AUTHORIZATION, ChatGptConfig.BEARER + ChatGptConfig.API_KEY);
        return new HttpEntity<>(requestDto, headers);
    }

    public ChatGptResponseDto getResponse(HttpEntity<ChatGptRequestDto> chatGptRequestDtoHttpEntity) {
        ResponseEntity<ChatGptResponseDto> responseEntity = restTemplate.postForEntity(
                ChatGptConfig.URL,
                chatGptRequestDtoHttpEntity,
                ChatGptResponseDto.class);
        return responseEntity.getBody();
    }

    public ChatGptResponseDto askQuestion(QuestionRequestDto requestDto) {
        String question = requestParsing(requestDto);
        System.out.println("시나리오 작성:"+question);
        return this.getResponse(
                this.buildHttpEntity(
                        new ChatGptRequestDto(
                                ChatGptConfig.MODEL,
                                question,
                                ChatGptConfig.MAX_TOKEN,
                                ChatGptConfig.TEMPERATURE,
                                ChatGptConfig.TOP_P
                        )
                )
        );
    }

    public ChatGptResponseDto askSummarize(SummarizingRequestDto requestDto){
        String question = requestParsingToSummarize(requestDto);
        System.out.println("한 줄 요약:"+question);
        return this.getResponse(
                this.buildHttpEntity(
                        new ChatGptRequestDto(
                                ChatGptConfig.MODEL,
                                question,
                                ChatGptConfig.MAX_TOKEN,
                                ChatGptConfig.TEMPERATURE,
                                ChatGptConfig.TOP_P
                        )
                )
        );
    }

    public String requestParsing(QuestionRequestDto requestDto){
//        return "'"+requestDto.getParameter1()+"',"
//                +"'"+requestDto.getParameter2()+"',"
//                +"'"+requestDto.getParameter3()+"'"
//                +"를 가지고 1문단에 100자이내, 총 3문단짜리 2~5세를 위한 동화를 영어로 만들어줘";
        return "Please make a fairy tale for 2-5 year olds with '"
        +requestDto.getParameter1()+"',"+"'"+requestDto.getParameter2()+"',"
        +"and '"+requestDto.getParameter3()+"'"+ "with less than 150 characters per paragraph";
    }

    public String requestParsingToSummarize(SummarizingRequestDto requestDto){
        String tmpText = requestDto.getText();
        String targetParagraph = "\n\n";
        int startIndex = tmpText.indexOf(targetParagraph);
        String parsedParagraph = "";
        if (startIndex != -1) {
            startIndex += targetParagraph.length();
            int endIndex = tmpText.indexOf("\n\n", startIndex);
            if (endIndex != -1) {
                parsedParagraph += tmpText.substring(startIndex, endIndex);
            }
        }
        //return "Summarize a line from"+"'"+parsedParagraph+"'";
        return "'"+parsedParagraph+"'"+"Please summarize a line in English";
    }
}