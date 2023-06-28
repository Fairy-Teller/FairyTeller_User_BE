package jungle.fairyTeller.fairyTale.Image.service;

import jungle.fairyTeller.fairyTale.Image.dto.ImgAIRequestDTO;
import jungle.fairyTeller.fairyTale.Image.dto.CreateImgResponseDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;


import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

@Slf4j
@Service
@PropertySource("classpath:application-image.properties")
@AllArgsConstructor
public class CreateImgService {

    @Value("${AI.SERVER.URL}")
    String aiServerUrl;

    private final ResourceLoader resourceLoader;

    @Autowired
    public CreateImgService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }


//    public String createImg(String prompt){
//        String fileName = "classpath:static/files/img.txt";
//        Resource resource = resourceLoader.getResource(fileName);
//        String result = "";
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                result += line;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return(result);
//    }

    public String createImg(String prompt){
        RestTemplate restTemplate = new RestTemplate();
        System.out.println("프롬프트 출력 : "  + prompt);
        String negative_prompt ="nsfw, (worst quality, low quality:1.4), text, signature, fat";
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

    public String addLora(int loraNo, String originalPrompt){
        switch (loraNo){
            case 1:
                return "<lora:COOLKIDS_MERGE_V2.5:1> " + originalPrompt;
            case 2:
                return "<lora:Colored_Icons:1> " + originalPrompt;
            case 3:
                return "<lora:doodle:1> " + originalPrompt;
            case 4:
                return "<lora:大坏狐狸的故事V1:1> " + originalPrompt;
            case 5:
                return "<lora:my-jrpencil:1> " + originalPrompt;
            case 6:
                return "<lora:playfulwhimsy-v2:1> " + originalPrompt;
            case 7:
                return "<lora:shuicai_v1:1> " + originalPrompt;
            case 8:
                return "<lora:IrisCompietStyle:1> " + originalPrompt;
            case 9:
                return "<lora:antonellafant:1> " + originalPrompt;
            case 10:
                return "<lora:bestiary_style:1> " + originalPrompt;
            case 11:
                return "<lora:janemassey:1> " + originalPrompt;
            default :
                return originalPrompt;
        }
    }

}
