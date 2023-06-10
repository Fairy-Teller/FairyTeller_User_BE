package jungle.fairyTeller.fairyTale.Image.controller;

import jungle.fairyTeller.fairyTale.Image.service.CreateImgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
public class CreateImgController {

    @Autowired
    private CreateImgService createImgService;

    @PostMapping("/images/craete")
    public ResponseEntity<Object> createImg(@RequestBody String prompt, @RequestBody String format){
        // 이미지 생성
        String base64Image = createImgService.createImg(prompt);
        HttpHeaders headers = new HttpHeaders();

        // 이미지 생성 및 형식에 따른 처리
        if (format.equalsIgnoreCase("base64")) {
            // JPG 형식으로 이미지 생성
            return new ResponseEntity<>(base64Image, headers, HttpStatus.OK);
        } else if (format.equalsIgnoreCase("jpg")) {
            // Base64 형식으로 이미지 생성
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            headers.setContentType(MediaType.valueOf("image/jpeg"));
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);

        } else {
            return ResponseEntity.badRequest().body("Invalid image format");
        }
    }
}
