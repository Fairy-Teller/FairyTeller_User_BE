package jungle.fairyTeller.fairyTale.story.service;

import jungle.fairyTeller.fairyTale.story.dto.translation.TranslationResponseDTO;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@PropertySource("classpath:application-papago.properties")
public class PaPagoTranslationService {

    public PaPagoTranslationService(){}

    @Value("${client-id}")
    private String client_id;
    @Value("${client-secret-key}")
    private String client_secret_key;
    @Value("${papago-url}")
    private String papago_url;

    public String translate(String text,String source, String target) {

        String textToTranslate = text; // 번역할 영어 문장

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("X-NCP-APIGW-API-KEY-ID", client_id);
        headers.set("X-NCP-APIGW-API-KEY", client_secret_key);

        // 요청 바디 설정
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("source", source);
        requestBody.add("target", target);
        requestBody.add("text", textToTranslate);
        requestBody.add("honorific","True"); //높임말 설정

        // HttpEntity 생성
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // RestTemplate 및 OkHttpClient 생성
        RestTemplate restTemplate = new RestTemplate();
        OkHttpClient okHttpClient = new OkHttpClient();

        // RestTemplate에 OkHttpClient 설정
        restTemplate.setRequestFactory(new OkHttp3ClientHttpRequestFactory(okHttpClient));

        // 번역 API 호출
        ResponseEntity<TranslationResponseDTO> responseEntity = restTemplate.exchange(
                papago_url,
                HttpMethod.POST,
                requestEntity,
                TranslationResponseDTO.class
        );

        // 번역 결과 추출
        TranslationResponseDTO translationResponse = responseEntity.getBody();
        String translatedText = translationResponse.getMessage().getResult().getTranslatedText();

        return translatedText;
    }
}
