package jungle.fairyTeller.fairyTale.Image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
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

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

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
        } catch( Exception e) {
            originalImage = null;
        }

        // 생성한 이미지를 가공한다. - 제목, 워터마크, 저자 이름
        Optional<UserEntity> userEntity = userService.getUserById(book.getAuthor());
        String author = userEntity.get().getNickname();

        byte[] coverImage = addObjectsToImage(originalImage, book.getTitle(), author);

        // 생성한 이미지를 s3에 저장한다.
        String thumbnailUrl = fileService.uploadFile(coverImage, book.getBookId()+ "_thumbnail.png");
        log.info(">>>s3에 저장 성공<<<");

        // 저장한 풀 url을 리턴한다.
        return thumbnailUrl;
    }

    private byte[] addObjectsToImage(byte[] image, String title, String author) {
        BufferedImage newImage;

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(image);
            BufferedImage bufferedImage = ImageIO.read(bais);

            if (bufferedImage == null) {
                log.error("Could not read image data into a BufferedImage.");
                return null;
            }

            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            String activeProfiles = environment.getProperty("spring.profiles.active");
            BufferedImage watermark;
            if (activeProfiles != null && activeProfiles.contains("dev")) {
                watermark = loadS3Image("logo_bright.png");
            } else {
                watermark = loadLocalImage(localUrl+"/logo_bright.png");
            }

            log.info(">>>watermark: " + watermark);

            // 새로운 이미지 생성
            newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

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

        } catch (Exception e) {
            return null;
        }

        try {
            return convertImageToBytes(newImage);
        } catch (Exception e) {
            log.error("Error occurred while converting image to bytes.", e);
            return null;
        }
    }

    private BufferedImage loadS3Image(String fileName) {
        try {
            S3Object s3object = amazonS3.getObject(new GetObjectRequest(bucket, fileName));
            S3ObjectInputStream inputStream = s3object.getObjectContent();
            BufferedImage img = ImageIO.read(inputStream);
            if (img == null) {
                log.error("Could not read watermark image at path: " + bucketUrl + "/"+ fileName);
            }
            return img;
        } catch (Exception e) {
            log.error("Error occurred while reading watermark image.", e);
        }
        return null;
    }
    private BufferedImage loadLocalImage(String imagePath) {
        try {
            BufferedImage img = ImageIO.read(new File(imagePath));
            if (img == null) {
                log.error("Could not read watermark image at path: " + imagePath);
            }
            return img;
        } catch (Exception e) {
            log.error("Error occurred while reading watermark image.", e);
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
