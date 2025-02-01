package com.pharmquest.pharmquest.global.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.pharmquest.pharmquest.global.data.S3File;
import com.pharmquest.pharmquest.global.repository.S3FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;
    private final S3FileRepository s3FileRepository;
    //s3 적용할때 db가 바뀌니까 관련 repository도 추가해야함
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // S3에 이미지 업로드 후 URL 반환
    public String uploadFile(MultipartFile file) {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename(); // 고유한 파일명 생성

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata);

            amazonS3.putObject(putObjectRequest);
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }

        String fileUrl = amazonS3.getUrl(bucketName, fileName).toString(); // 업로드된 파일 URL 반환

        // DB에 저장 (향후 이용시 db를 수정해야함 S3Service안에 업로드가 필요한 메소드 작성할 때 db명도 맞게 수정하면 됨)
        S3File s3File = S3File.builder()
                .fileName(fileName)
                .fileUrl(fileUrl)
                .build();
        s3FileRepository.save(s3File);

        return fileUrl;
    }

    //단순 조회용도 테스트 목적
    public List<S3File> getAllFiles() {
        return s3FileRepository.findAll();
    }


}
