package jungle.fairyTeller.fairyTale.file.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.bucket.url}")
    private String bucketUrl;

    @Value("${upload.path}")
    private String localUrl;

    private final AmazonS3Client amazonS3Client;

    private final Environment environment;

    public String uploadFile(byte[] file, String fileName) {
        log.info(">>>uploadFile 호출");
        // 프로필에 따라 S3 또는 로컬 파일 시스템에 파일 업로드
        String activeProfiles = environment.getProperty("spring.profiles.active");
        log.info(">>>profile = "+activeProfiles);
        String filePath;
        if (activeProfiles != null && activeProfiles.contains("dev")) {
            log.info(">>>dev if 문에 걸렸다");
            filePath = uploadToS3(file, fileName);
            log.info("property - dev");
        } else {
            filePath = uploadToLocal(file, fileName);
        }
        log.info(">>>저장경로: "+filePath);
        return filePath;
    }

    private String uploadToS3(byte[] file, String fileName) {
        log.info("Entered uploadToS3 method");
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.length);
            log.info(">>>>put 전<<<<");

            amazonS3Client.putObject(bucket, fileName, new ByteArrayInputStream(file), metadata);
            log.info(">>>put 후<<<<");

            return bucketUrl +"/" + fileName;
        } catch (AmazonS3Exception e) {
            log.error("Failed to upload file to S3: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file to S3", e);
        } catch (Exception e) {
            log.error("General error: {}", e.getMessage());
            throw e;
        }
    }

    private String uploadToLocal(byte[] file, String fileName) {
        String localFilePath = localUrl + "/" + fileName;

        File directory = new File(localUrl);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File localFile = new File(localFilePath);
        try (FileOutputStream out = new FileOutputStream(localFile)) {
            out.write(file);
            log.info("Content written to file " + localFilePath);
        } catch (IOException e) {
            log.error("Failed to upload file locally: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file locally", e);
        }

        return localFilePath;
    }
}
