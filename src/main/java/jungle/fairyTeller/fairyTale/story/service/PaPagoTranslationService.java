package jungle.fairyTeller.fairyTale.story.service;

import com.google.api.client.util.Value;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@PropertySource("classpath:application-local.properties")
public class TranslationService {

    public TranslationService(){}

//    @Value("${client-id}")
//    private String client_id;
//    @Value("${client-secret-key}")
//    private String client_secret_key;
//    @Value("${papago-url}")
//    private String papago_url;

    public String translate(String text) {

         String client_id="jhwam836ns";
         String client_secret_key = "2BGDAPs194WKiTJMsIvHbaLK8EW5TEijoY3VNQGX";
         String papago_url = "https://naveropenapi.apigw.ntruss.com/nmt/v1/translation";

        String textToTranslate = text; // 번역할 영어 문장

        System.out.println("client_id: "+client_id);
        System.out.println("client_secret_key: "+client_secret_key);
        System.out.println("papago_url: "+papago_url);

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("X-NCP-APIGW-API-KEY-ID", client_id);
        headers.set("X-NCP-APIGW-API-KEY", client_secret_key);

        // 요청 바디 설정
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("source", "en");
        requestBody.add("target", "ko");
        requestBody.add("text", textToTranslate);

        // HttpEntity 생성
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // RestTemplate 및 OkHttpClient 생성
        RestTemplate restTemplate = new RestTemplate();
        OkHttpClient okHttpClient = new OkHttpClient();

        // RestTemplate에 OkHttpClient 설정
        restTemplate.setRequestFactory(new OkHttp3ClientHttpRequestFactory(okHttpClient));

        // 번역 API 호출
        ResponseEntity<TranslationResponse> responseEntity = restTemplate.exchange(
                papago_url,
                HttpMethod.POST,
                requestEntity,
                TranslationResponse.class
        );

        // 번역 결과 추출
        TranslationResponse translationResponse = responseEntity.getBody();
        String translatedText = translationResponse.getMessage().getResult().getTranslatedText();

        return translatedText;
    }

    // 번역 결과를 매핑하기 위한 POJO 클래스
    private static class TranslationResponse {
        private Message message;

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }
    }

    private static class Message {
        private Result result;

        public Result getResult() {
            return result;
        }

        public void setResult(Result result) {
            this.result = result;
        }
    }

    private static class Result {
        private String translatedText;

        public String getTranslatedText() {
            return translatedText;
        }

        public void setTranslatedText(String translatedText) {
            this.translatedText = translatedText;
        }
    }
}
