package jungle.fairyTeller.fairyTale.story.entity;

public enum KeywordEnum {
    PRINCESS("공주"),
    PRINCE("왕자"),
    TIGER("호랑이"),
    WIZARD("마법사"),
    RAINBOW("무지개"),
    DINOSAUR("공룡"),
    JEWEL("보석"),
    RABBIT("토끼"),
    SOCKS("양말"),
    CHOCOLATE("초콜릿");

    private final String value;

    KeywordEnum(String value){
        this.value = value;
    }
    public String getValue(){
        return value;
    }
}
