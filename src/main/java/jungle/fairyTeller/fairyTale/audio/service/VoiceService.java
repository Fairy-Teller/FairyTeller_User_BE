package jungle.fairyTeller.fairyTale.audio.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

@Slf4j
@Service
public class VoiceService {

    public byte[] convertBase64ToAudio(String base64Audio) throws IOException {
        if (Objects.isNull(base64Audio) || base64Audio.isEmpty()) {
            throw new IllegalArgumentException("Base64 image is empty or null");
        }

        // Remove data URL prefix (e.g., "data:image/png;base64,")
        //String base64Data = base64Audio.replaceAll("^data:image/[a-zA-Z]+;base64,", "");
        String base64Data = base64Audio;

        byte[] imageBytes = Base64.getDecoder().decode(base64Data);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        return bis.readAllBytes();
    }
}
