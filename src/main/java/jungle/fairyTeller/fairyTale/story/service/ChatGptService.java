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

import java.io.PrintStream;
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

        String word1= "";
        String word2= "";
        String word3= "";
        String word4= "";
        String word5= "";

        if(!requestDto.getParameter1().isEmpty()){
            word1 = requestDto.getParameter1();
        }
        if(requestDto.getParameter2() != null && !requestDto.getParameter2().isEmpty()){
            word2 = requestDto.getParameter2();
        }
        if(requestDto.getParameter3() != null &&!requestDto.getParameter3().isEmpty()){
            word3 = requestDto.getParameter3();
        }
        if(requestDto.getParameter4() != null &&!requestDto.getParameter4().isEmpty()){
            word4 = requestDto.getParameter4();
        }
        if(requestDto.getParameter5() != null &&!requestDto.getParameter5().isEmpty()){
            word5 = requestDto.getParameter5();
        }


//        return "Please make a happy fairy tale for 2-5 years old with '"
//        +requestDto.getParameter1()+"',"+"'"+requestDto.getParameter2()+"',"
//        +"'"+requestDto.getParameter3()+"',"+"'"+requestDto.getParameter4()
//        +",'"+requestDto.getParameter5()+"'"
//        + "in English";

        return "Please make a happy fairy tale for 2-5 years old with '"
                +word1+"',"+"'"+word2+"',"
                +"'"+word3+"',"+"'"+word4+"'"
                +",'"+word5+"'"
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
        int paragraphSize = (int) Math.round((double) sentences.length / 5); // 5개의 문단으로 분할

        System.out.println(sentences.length);
        System.out.println(paragraphSize);

        int cnt = 0;
        for (int i = 0; i < sentences.length; i += paragraphSize) {
            System.out.println("cnt:"+cnt);
            cnt++;
            int endIndex = 0;
            if(cnt == 5){
                endIndex = sentences.length;
            }
            else{
                endIndex = Math.min(i + paragraphSize, sentences.length);
            }
            String[] paragraphSentences = Arrays.copyOfRange(sentences, i, endIndex);

            HashMap<String,Object> divideParagraph = new HashMap<>();
            String paragraph = String.join(".\n", paragraphSentences) + "."; // 문장을 다시 문단으로 결합
            divideParagraph.put("paragraph",paragraph);
            paragraphs.add(divideParagraph);
        }
        if(paragraphs.size() == 6){
            paragraphs.remove(5);
        }
        return paragraphs;
    }

    public QuestionRequestDto koreanEnglishMapping(QuestionRequestDto requestDto){
        HashMap<String,String> mapping = new HashMap<>();
        mapping.put("공주","princess");
        mapping.put("왕자","prince");
        mapping.put("엄마","mom");
        mapping.put("아빠","dad");
        mapping.put("요정","fairy");
        mapping.put("화가","artist");
        mapping.put("요리사","chef");
        mapping.put("경찰관","police officer");
        mapping.put("선생님","teacher");
        mapping.put("개발자","developer");

        mapping.put("토끼","rabbit");
        mapping.put("강아지","dog");
        mapping.put("고양이","cat");
        mapping.put("사자","lion");
        mapping.put("돼지","pig");
        mapping.put("펭귄","penguin");
        mapping.put("상어","shark");
        mapping.put("오리","duck");
        mapping.put("코끼리","elephant");
        mapping.put("공룡","dinosaur");
        mapping.put("말","horse");

        mapping.put("분홍색","pink");
        mapping.put("노랑색","yellow");
        mapping.put("하늘색","sky blue");
        mapping.put("초록색","green");
        mapping.put("보라색","purple");
        mapping.put("흰색","white");
        mapping.put("무지개색","rainbow");
        mapping.put("주황색","orange");
        mapping.put("금색","gold");
        mapping.put("은색","silver");

        mapping.put("소방차","fire engine");
        mapping.put("경찰차","police car");
        mapping.put("반지","ring");
        mapping.put("선물","gift");
        mapping.put("핸드폰","cell phone");
        mapping.put("사탕","candy");
        mapping.put("구름","cloud");
        mapping.put("솜사탕","cotton candy");
        mapping.put("드레스","dress");
        mapping.put("과자","snack");
        mapping.put("왕관","crown");
        mapping.put("컴퓨터","computer");

        mapping.put("바다","sea");
        mapping.put("유치원","kindergarten");
        mapping.put("숲","forest");
        mapping.put("학교","school");
        mapping.put("산","mountain");
        mapping.put("집","house");
        mapping.put("궁전","palace");
        mapping.put("박물관","museum");
        mapping.put("놀이터","playground");
        mapping.put("놀이동산","amusement park");
        mapping.put("동물원","zoo");

        QuestionRequestDto questionRequestDto = new QuestionRequestDto();

        if(!requestDto.getParameter1().isEmpty()){
            String transWord1 = mapping.get(requestDto.getParameter1());
            questionRequestDto.setParameter1(transWord1);
        }
        if(!requestDto.getParameter2().isEmpty()){
            String transWord2 = mapping.get(requestDto.getParameter2());
            questionRequestDto.setParameter2(transWord2);
        }
        if(!requestDto.getParameter3().isEmpty()){
            String transWord3 = mapping.get(requestDto.getParameter3());
            questionRequestDto.setParameter3(transWord3);
        }
        if(!requestDto.getParameter4().isEmpty()){
            String transWord4 = mapping.get(requestDto.getParameter4());
            questionRequestDto.setParameter4(transWord4);
        }
        if(!requestDto.getParameter5().isEmpty()){
            String transWord5 = mapping.get(requestDto.getParameter5());
            questionRequestDto.setParameter5(transWord5);
        }
        return questionRequestDto;
    }
}