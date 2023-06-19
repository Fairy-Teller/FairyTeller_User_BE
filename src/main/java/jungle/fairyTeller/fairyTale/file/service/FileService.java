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
        // 프로필에 따라 S3 또는 로컬 파일 시스템에 파일 업로드
        String activeProfiles = environment.getProperty("spring.profiles.active");
        String filePath;
        if (activeProfiles != null && activeProfiles.contains("dev")) {
            filePath = uploadToS3(file, fileName);
        } else {
            filePath = uploadToLocal(file, fileName);
        }
        return filePath;
    }

    private String uploadToS3(byte[] file, String fileName) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.length);

            amazonS3Client.putObject(bucketUrl, fileName, new ByteArrayInputStream(file), metadata);

            return bucketUrl +"/" + fileName;
        } catch (AmazonS3Exception e) {
            log.error("Failed to upload file to S3: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file to S3", e);
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
            log.info("Audio content written to file " + localFilePath);
        } catch (IOException e) {
            log.error("Failed to upload file locally: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file locally", e);
        }

        return localFilePath;
    }
}
