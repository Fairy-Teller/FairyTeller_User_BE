package jungle.fairyTeller.config;

public class ChatGptConfig {
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String API_KEY = "<인증키,24시간마다 새로 발급받야함>";
    public static final String MODEL = "text-davinci-003";
    public static final Integer MAX_TOKEN = 3978;
    public static final Double TEMPERATURE = 0.0;
    public static final Double TOP_P = 1.0;
    public static final String MEDIA_TYPE = "application/json; charset=UTF-8";
    public static final String URL = "https://api.openai.com/v1/completions";
}
