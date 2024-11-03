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

    // Blob 데이터가 포함된 MultipartFile을 File로 변환
    Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(System.getProperty("user.home") + "/" + file.getOriginalFilename());

        // 새로운 파일을 생성하여 Blob 데이터를 파일로 저장
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes()); // Blob 데이터를 바이트 배열로 저장
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
