package jungle.fairyTeller.fairyTale.Image.service;

import jungle.fairyTeller.fairyTale.book.entity.BookEntity;
import jungle.fairyTeller.fairyTale.file.service.FileService;
import jungle.fairyTeller.user.entity.UserEntity;
import jungle.fairyTeller.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Optional;
import javax.imageio.ImageIO;


@Slf4j
@Service
@RequiredArgsConstructor
public class ThumbnailService {

    @Autowired
    private CreateImgService createImgService;

    @Autowired
    private SaveImgService saveImgService;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    private final Environment environment;

    @Value("${cloud.aws.s3.bucket.url}")
    private String bucketUrl;

    @Value("${upload.path}")
    private String localUrl;

    public String createThumbnail(BookEntity book){
        // 표지 이미지를 생성한다.
        String base64Data = createImgService.createImg(book.getTitle()); // base64 String 그 자체
        String base64Image = base64Data.replaceAll("^data:image/[a-zA-Z]+;base64,", "");

        byte[] originalImage;
        try {
            originalImage = saveImgService.convertBase64ToImage(base64Image);
            log.info(">>>표지 이미지는 생성 완료<<<");
        } catch( Exception e) {
            originalImage = null;
        }

        // 생성한 이미지를 가공한다. - 제목, 워터마크, 저자 이름
        Optional<UserEntity> userEntity = userService.getUserById(book.getAuthor());
        String author = userEntity.get().getNickname();

        byte[] coverImage = addObjectsToImage(originalImage, book.getTitle(), author);
        log.info(">>>가공된 표지 이미지 생성 완료<<<");

        // 생성한 이미지를 s3에 저장한다.
        String thumbnailUrl = fileService.uploadFile(coverImage, book.getBookId().toString() + "_thumbnail");
        log.info(">>>s3에 저장 성공<<<");

        // 저장한 풀 url을 리턴한다.
        return thumbnailUrl;
    }

    private byte[] addObjectsToImage(byte[] image, String title, String author) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(image);
            BufferedImage bufferedImage = ImageIO.read(bais);

            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            String activeProfiles = environment.getProperty("spring.profiles.active");
            BufferedImage watermark;
            if (activeProfiles != null && activeProfiles.contains("dev")) {
                watermark = loadImage(bucketUrl+"/logo_bright.png");
            } else {
                watermark = loadImage(localUrl+"/logo_bright.png");
            }

            // 새로운 이미지 생성
            BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            // Graphics2D 객체 생성
            Graphics2D g2d = newImage.createGraphics();

            // 원본 이미지를 새 이미지로 그리기
            g2d.drawImage(bufferedImage, 0, 0, null);

            // 제목 설정
            String fontName = "Arial";
            int fontSize = 30;
            Font font = new Font(fontName, Font.BOLD, fontSize);
            g2d.setFont(font);
            g2d.setColor(Color.WHITE);

            // 제목을 이미지 중앙에 그리기
            int titleWidth = g2d.getFontMetrics().stringWidth(title);
            int title_x = (width - titleWidth) / 2;
            int title_y = height / 3;
            g2d.drawString(title, title_x, title_y);

            // 저자 설정
            fontSize = 24;
            font = new Font(fontName, Font.BOLD, fontSize);
            g2d.setFont(font);

            // 저자 그리기
            int authorWidth = g2d.getFontMetrics().stringWidth(author);
            int author_x = ((width - authorWidth) / 4 ) * 3;
            int author_y = height / 2;
            g2d.drawString(author, author_x, author_y);

            // 로고를 우하단에 표시하기
            int watermarkWidth = watermark.getWidth();
            int watermarkHeight = watermark.getHeight();
            int watermarkX = width - watermarkWidth - 3;
            int watermarkY = height - watermarkHeight - 3;
            g2d.drawImage(watermark, watermarkX, watermarkY, null);

            // Graphics2D 자원 해제
            g2d.dispose();

            return convertImageToBytes(newImage);
        } catch (Exception e) {
            return null;
        }
    }

    private BufferedImage loadImage(String imagePath) {
        try {
            return ImageIO.read(new File(imagePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] convertImageToBytes(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error occurred while converting image to bytes.", e);
        }
        return null;
    }
}
