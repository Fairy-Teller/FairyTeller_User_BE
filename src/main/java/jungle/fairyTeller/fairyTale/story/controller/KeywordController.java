package jungle.fairyTeller.fairyTale.story.controller;

import jungle.fairyTeller.fairyTale.story.dto.KeywordOptionDto;
import jungle.fairyTeller.fairyTale.story.entity.KeywordEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class KeywordController {
    @GetMapping("/keyword")
    public ResponseEntity<?> getKeywords(@AuthenticationPrincipal String userId){
        List<Map<String,Object>> options = new ArrayList<>();

        Map<String , Object> peopleOptions = new HashMap<>();
        peopleOptions.put("theme", "PEOPLE");
        peopleOptions.put("titles", Arrays.asList("공주", "왕자", "엄마", "아빠", "요정", "화가", "요리사",
                "경찰관", "선생님", "개발자"));
        options.add(peopleOptions);

        Map<String , Object> animalOptions = new HashMap<>();
        animalOptions.put("theme", "ANIMAL");
        animalOptions.put("titles", Arrays.asList("토끼","강아지", "고양이", "사자","돼지",
                "펭귄","호랑이", "병아리", "사슴","공룡","말"));
        options.add(animalOptions);

        Map<String , Object> colorOptions = new HashMap<>();
        colorOptions.put("theme", "COLOR");
        colorOptions.put("titles", Arrays.asList("분홍색", "노랑색", "하늘색", "초록색", "보라색",
                "흰색", "무지개색", "주황색", "금색", "은색"));
        options.add(colorOptions);

        Map<String , Object> thingOptions = new HashMap<>();
        thingOptions.put("theme", "THING");
        thingOptions.put("titles", Arrays.asList("소방차", "경찰차", "반지", "선물", "핸드폰", "사탕",
                "구름", "솜사탕", "드레스", "구두", "왕관", "컴퓨터"));
        options.add(thingOptions);

        Map<String , Object> placeOptions = new HashMap<>();
        placeOptions.put("theme", "PLACE");
        placeOptions.put("titles", Arrays.asList("바다", "유치원", "숲", "학교", "정원", "집", "궁전",
                "공원", "놀이터", "놀이동산", "동물원"));
        options.add(placeOptions);

        return ResponseEntity.ok().body(options);
    }
}