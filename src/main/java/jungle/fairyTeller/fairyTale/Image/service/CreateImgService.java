package jungle.fairyTeller.fairyTale.Image.service;

import jungle.fairyTeller.fairyTale.Image.dto.ImgAIRequestDTO;
import jungle.fairyTeller.fairyTale.Image.dto.CreateImgResponseDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;

@Slf4j
@Service
@PropertySource("classpath:application-image.properties")
@AllArgsConstructor
public class CreateImgService {
    public CreateImgService(){}
    @Value("${AI.SERVER.URL}")
    String aiServerUrl;

    public String createImg(String prompt){
        RestTemplate restTemplate = new RestTemplate();
        System.out.println("프롬프트 출력 : "  + prompt);
        String negative_prompt ="nsfw, (worst quality, low quality:1.4), text, signature, fat, (worst quality, low quality:1.4), text, signature, fat";
        // POST 요청에 필요한 데이터를 객체에 담기
        ImgAIRequestDTO requestObject = new ImgAIRequestDTO(1,7,0,false,0,0,0,405,1,negative_prompt,prompt,false,0,1,0,0,"Euler a",-1,-1,-1,30,new ArrayList(),-1,0,false,720);

        // 요청 헤더 설정 (선택적)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 헤더에 필요한 정보 추가
//         headers.set("HeaderName", "HeaderValue");
        // 요청 객체 생성
        HttpEntity<ImgAIRequestDTO> requestEntity = new HttpEntity<>(requestObject, headers);

        // POST 요청 보내기
        ResponseEntity<CreateImgResponseDTO> response = restTemplate.postForEntity(aiServerUrl + "/sdapi/v1/txt2img", requestEntity, CreateImgResponseDTO.class);

        // 응답 받기
        CreateImgResponseDTO responseBody = response.getBody();

        return responseBody.getImages().get(0);
    }

}
