package jungle.fairyTeller.fairyTale.Image.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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

    public boolean isImageDark(byte[] imageBytes) {
        BufferedImage image;
        int red = 0;
        int green = 0;
        int blue = 0;
        int pixelCount = 0;

        try {
            image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    int clr = image.getRGB(x, y);
                    red += (clr & 0x00ff0000) >> 16;
                    green += (clr & 0x0000ff00) >> 8;
                    blue += clr & 0x000000ff;
                    pixelCount++;
                }
            }

            // Calculate the average color of the image.
            red = red / pixelCount;
            green = green / pixelCount;
            blue = blue / pixelCount;

        } catch (IOException e) {
            log.error("Error occurred while reading image data.", e);
            return false;
        }

        // Define a darkness threshold. It can be adjusted according to your needs.
        int darkThreshold = 120;
        // Calculate the average color intensity.
        int averageColorIntensity = (red + green + blue) / 3;
        log.info("average color: " + averageColorIntensity);

        // Check if the image is dark.
        return averageColorIntensity <= darkThreshold;
    }

}
