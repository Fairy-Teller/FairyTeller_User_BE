package jungle.fairyTeller.fairyTale.audio;

import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import com.google.cloud.texttospeech.v1.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@Service
public class TtsService {
    @Value("${google.credentials.path}")
    private String googleCredentialsPath;

    public String synthesizeText(String text, String fileName) throws Exception {
        String fullPath = null;
        String directoryPath = "/Users/hjilee/Desktop/hj/SWJungle/namanmoo/fairyTeller/BE/test_audio";

        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            // 입력 텍스트 설정
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

            // 음성 타입과 언어 설정
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("en-US") // 언어 코드
                    .setSsmlGender(SsmlVoiceGender.FEMALE) // 음성 타입
                    .build();

            // 음성 출력 형식 설정 (MP3)
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();

            // TTS 요청
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // 응답 데이터를 Byte Stream으로 변환
            ByteString audioContents = response.getAudioContent();

            // 음성 데이터를 s3 특정 경로에 저장
            fullPath = directoryPath + File.separator + fileName +".mp3";

            try (OutputStream out = new FileOutputStream(fullPath)) {
                out.write(audioContents.toByteArray());
                log.info("Audio content written to file " + fullPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fullPath;
    }
}
