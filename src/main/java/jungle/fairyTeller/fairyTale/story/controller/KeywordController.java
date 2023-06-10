package jungle.fairyTeller.fairyTale.story.controller;

import jungle.fairyTeller.fairyTale.story.entity.KeywordEnum;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KeywordController {
    @GetMapping("/fairytale")
    public KeywordEnum[] getKeywords(@AuthenticationPrincipal String userId){
        return KeywordEnum.values();
    }
}