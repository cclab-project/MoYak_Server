package study.moyak.chat.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class S3Service {


    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // Base64 데이터를 파일로 변환
    Optional<File> convert(String base64Image) throws IOException {
        // 홈 디렉토리에 저장할 파일 이름을 설정 (예: "uploadedImage.jpg")
        File convertFile = new File(System.getProperty("user.home") + "/uploadedImage.jpg");

        // Base64 디코딩하여 바이트 배열로 변환
        byte[] decodedBytes = Base64.getDecoder().decode(base64Image);

        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(decodedBytes); // 디코딩된 바이트 배열을 파일로 저장
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    // S3로 업로드
    String uploadS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(
                CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }
}
