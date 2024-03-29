package jungle.fairyTeller.fairyTale.story.service;

import jungle.fairyTeller.fairyTale.story.dto.ChatGptRequestDto;
import jungle.fairyTeller.fairyTale.story.dto.ChatGptResponseDto;
import jungle.fairyTeller.fairyTale.story.dto.QuestionRequestDto;
import jungle.fairyTeller.config.ChatGptConfig;
import jungle.fairyTeller.fairyTale.story.dto.SummarizingRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatGptService {

    private static RestTemplate restTemplate = new RestTemplate();
    private final ChatGptConfig chatGptConfig;

    @Autowired
    public ChatGptService(ChatGptConfig chatGptConfig) {
        this.chatGptConfig = chatGptConfig;
    }

    public HttpEntity<ChatGptRequestDto> buildHttpEntity(ChatGptRequestDto requestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(chatGptConfig.getMediaType()));
        headers.add(chatGptConfig.getAuthorization(), chatGptConfig.getBearer() + chatGptConfig.getApiKey());
        return new HttpEntity<>(requestDto, headers);
    }

    public ChatGptResponseDto getResponse(HttpEntity<ChatGptRequestDto> chatGptRequestDtoHttpEntity) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + chatGptConfig.getApiKey());

        HttpEntity<ChatGptRequestDto> requestEntity = new HttpEntity<>(chatGptRequestDtoHttpEntity.getBody(), headers);

        ResponseEntity<ChatGptResponseDto> responseEntity = restTemplate.postForEntity(
                chatGptConfig.getUrl(),
                requestEntity,
                ChatGptResponseDto.class);

        return responseEntity.getBody();
    }

    public ChatGptResponseDto askQuestion(QuestionRequestDto requestDto) {
        String question = requestParsing(requestDto);
        System.out.println("시나리오 작성:"+question);
        return this.getResponse(
                this.buildHttpEntity(
                        new ChatGptRequestDto(
                                chatGptConfig.getModel(),
                                question,
                                chatGptConfig.getMaxToken(),
                                chatGptConfig.getTemperature(),
                                chatGptConfig.getTopP()
                        )
                )
        );
    }

    public ChatGptResponseDto askSummarize(SummarizingRequestDto requestDto){
        String question = requestParsingToSummarize(requestDto);
        //System.out.println("한 줄 요약:"+requestDto.getText());
        //String question = "'"+requestDto.getText()+"'"+"Please summarize a line in English";
        return this.getResponse(
                this.buildHttpEntity(
                        new ChatGptRequestDto(
                                chatGptConfig.getModel(),
                                question,
                                chatGptConfig.getMaxToken(),
                                chatGptConfig.getTemperature(),
                                chatGptConfig.getTopP()
                        )
                )
        );
    }

    public String requestParsing(QuestionRequestDto requestDto){
        return "Please make a fairy tale for 2-5 year olds with '"
        +requestDto.getParameter1()+"',"+"'"+requestDto.getParameter2()+"',"
        +"and '"+requestDto.getParameter3()+"'"+ "with less than 150 characters per paragraph in English";
    }

    public String requestParsingToSummarize(SummarizingRequestDto requestDto){
        String tmpText = requestDto.getText();
        tmpText = tmpText.replaceAll("\n","");

        return "'"+tmpText+"'"+"Please summarize a line in English";
    }
}