package jungle.fairyTeller.fairyTale.story.controller;

import jungle.fairyTeller.fairyTale.story.dto.KeywordOptionDto;
import jungle.fairyTeller.fairyTale.story.entity.KeywordEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class KeywordController {
    @GetMapping("/keyword")
    public ResponseEntity<?> getKeywords(@AuthenticationPrincipal String userId){
        List<KeywordOptionDto> options = new ArrayList<>();

        options.add(new KeywordOptionDto(KeywordEnum.ANIMAL, "강아지"));
        options.add(new KeywordOptionDto(KeywordEnum.ANIMAL, "고양이"));
        options.add(new KeywordOptionDto(KeywordEnum.ANIMAL, "공룡"));
        options.add(new KeywordOptionDto(KeywordEnum.ANIMAL, "개구리"));
        options.add(new KeywordOptionDto(KeywordEnum.ANIMAL, "여우"));
        options.add(new KeywordOptionDto(KeywordEnum.ANIMAL, "판다"));
        options.add(new KeywordOptionDto(KeywordEnum.PEOPLE, "엄마"));
        options.add(new KeywordOptionDto(KeywordEnum.PEOPLE, "아빠"));
        options.add(new KeywordOptionDto(KeywordEnum.PEOPLE, "경찰관"));
        options.add(new KeywordOptionDto(KeywordEnum.PEOPLE, "의사"));
        options.add(new KeywordOptionDto(KeywordEnum.PEOPLE, "요리사"));
        options.add(new KeywordOptionDto(KeywordEnum.PEOPLE, "선생님"));

        return ResponseEntity.ok().body(options);
    }
}