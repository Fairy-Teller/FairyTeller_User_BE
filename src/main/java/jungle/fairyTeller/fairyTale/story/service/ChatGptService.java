package jungle.fairyTeller.fairyTale.story.service;

import jungle.fairyTeller.fairyTale.story.dto.chatGpt.ChatGptRequestDto;
import jungle.fairyTeller.fairyTale.story.dto.chatGpt.ChatGptResponseDto;
import jungle.fairyTeller.fairyTale.story.dto.chatGpt.QuestionRequestDto;
import jungle.fairyTeller.config.ChatGptConfig;
import jungle.fairyTeller.fairyTale.story.dto.SummarizingRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service
public class ChatGptService {

    private static RestTemplate restTemplate = new RestTemplate();
    private final ChatGptConfig chatGptConfig;

    @Autowired
    public ChatGptService(ChatGptConfig chatGptConfig) {
        this.chatGptConfig = chatGptConfig;
    }
    @Autowired
    public PaPagoTranslationService paPagoTranslationService;

    public HttpEntity<ChatGptRequestDto> buildHttpEntity(ChatGptRequestDto requestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(chatGptConfig.getMediaType()));
        headers.add(chatGptConfig.getAuthorization(), chatGptConfig.getBearer() + chatGptConfig.getApiKey());
        return new HttpEntity<>(requestDto, headers);
    }

    public HttpEntity<List<HashMap<String, Object>>> getResponse(HttpEntity<ChatGptRequestDto> chatGptRequestDtoHttpEntity) {

        String source = "en";
        String target = "ko";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + chatGptConfig.getApiKey());

        HttpEntity<ChatGptRequestDto> requestEntity = new HttpEntity<>(chatGptRequestDtoHttpEntity.getBody(), headers);

        //gpt 응답 받음
        ChatGptResponseDto responseDto = restTemplate.postForEntity(
                chatGptConfig.getUrl(),
                requestEntity,
                ChatGptResponseDto.class).getBody();

        //이야기 번역(en->ko)
        String tmp = clearString(responseDto.getText());
        String translateToText = paPagoTranslationService.translate(tmp,source,target);
        List<HashMap<String,Object>> divideParagraphs = divideIntoParagraphs(translateToText);

        responseDto.setText(translateToText);
        HttpEntity<List<HashMap<String, Object>>> httpdivideParagraphs = new HttpEntity<>(divideParagraphs);

        return httpdivideParagraphs;
    }

    public ChatGptResponseDto tmpGetResponseToSummarize(HttpEntity<ChatGptRequestDto> chatGptRequestDtoHttpEntity) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + chatGptConfig.getApiKey());

        HttpEntity<ChatGptRequestDto> requestEntity = new HttpEntity<>(chatGptRequestDtoHttpEntity.getBody(), headers);

        ChatGptResponseDto responseDto = restTemplate.postForEntity(
                chatGptConfig.getUrl(),
                requestEntity,
                ChatGptResponseDto.class).getBody();

        return responseDto;
    }
    public HttpEntity<List<HashMap<String, Object>>> askQuestion(QuestionRequestDto requestDto) {
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
        return this.tmpGetResponseToSummarize(
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
        +"'"+requestDto.getParameter3()+"',"+"'"+requestDto.getParameter4()
        +",'"+requestDto.getParameter5()+"'"
        + "in English";
    }

    public String requestParsingToSummarize(SummarizingRequestDto requestDto){
        String tmpText = requestDto.getText();
        tmpText = tmpText.replaceAll("\n","");

        return "'"+tmpText+"'"+"Please summarize a line in English";
    }

    public String clearString(String text){
        String parsingText = text;

        parsingText = parsingText.replaceAll("\\n\\n","");
        parsingText = parsingText.replaceAll("\\. ",".");
        parsingText = parsingText.replaceAll("The End.","");
        parsingText = parsingText.replaceAll("The end.","");
        parsingText = parsingText.replaceAll("Once upon a time, ", "");
        return parsingText;
    }

    //1. '.' 카운트해서 몇 문장인지 확인
    public int countDots(String text){
        int cnt = 0;
        for(int i=0; i<text.length();i++){
            if (text.charAt(i) == '.') cnt++;
        }
        System.out.println("cnt: "+cnt);
        return cnt;
    }
    public List<HashMap<String,Object>> divideIntoParagraphs(String text) {
        List<HashMap<String,Object>> paragraphs = new ArrayList<>();
        String[] sentences = text.split("\\.\\s*"); // 마침표를 기준으로 문장 분리

        int paragraphSize = (int) Math.ceil((double) sentences.length / 5); // 5개의 문단으로 분할

        for (int i = 0; i < sentences.length; i += paragraphSize) {
            int endIndex = Math.min(i + paragraphSize, sentences.length);
            String[] paragraphSentences = Arrays.copyOfRange(sentences, i, endIndex);

            HashMap<String,Object> divideParagraph = new HashMap<>();
            String paragraph = String.join(". ", paragraphSentences) + "."; // 문장을 다시 문단으로 결합
            divideParagraph.put("paragraph",paragraph);
            paragraphs.add(divideParagraph);
        }
        return paragraphs;
    }

}