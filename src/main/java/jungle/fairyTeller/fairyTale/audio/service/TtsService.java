package jungle.fairyTeller.fairyTale.audio.service;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import com.google.cloud.texttospeech.v1.*;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.Collections;

@Slf4j
@Service
public class TtsService {
    @Value("${google.credentials.path}")
    private final String googleCredentialsPath;

    public TtsService(@Value("${google.credentials.path}") String googleCredentialsPath) {
        this.googleCredentialsPath = googleCredentialsPath;
    }

    public byte[] synthesizeText(String text, String fileName) throws Exception {

        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(googleCredentialsPath));

        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create(TextToSpeechSettings.newBuilder().setCredentialsProvider(() -> credentials).build())) {
            // 입력 텍스트 설정
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

            // 음성 타입과 언어 설정
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("ko-KR") // 언어 코드
                    .setSsmlGender(SsmlVoiceGender.FEMALE) // 여성 음성 타입
                    .build();


            // 음성 출력 형식 설정 (MP3)
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();

            // TTS 요청
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // 응답 데이터를 Byte Stream으로 변환
            ByteString audioContents = response.getAudioContent();
            return audioContents.toByteArray();
        }
    }
}
