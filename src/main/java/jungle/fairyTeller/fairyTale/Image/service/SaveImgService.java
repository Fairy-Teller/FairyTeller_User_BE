package jungle.fairyTeller.fairyTale.Image.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

@Slf4j
@Service
public class SaveImgService {

    public byte[] convertBase64ToImage(String base64Image) throws IOException {
        if (Objects.isNull(base64Image) || base64Image.isEmpty()) {
            throw new IllegalArgumentException("Base64 image is empty or null");
        }

        // Remove data URL prefix (e.g., "data:image/png;base64,")
        String base64Data = base64Image.replaceAll("^data:image/[a-zA-Z]+;base64,", "");

        byte[] imageBytes = Base64.getDecoder().decode(base64Data);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        return bis.readAllBytes();
    }
}
