package jungle.fairyTeller.fairyTale.story.controller;

import jungle.fairyTeller.fairyTale.story.entity.KeywordEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KeywordController {
    @GetMapping("/fairytale")
    public KeywordEnum[] getKeywords(){
        return KeywordEnum.values();
    }
}