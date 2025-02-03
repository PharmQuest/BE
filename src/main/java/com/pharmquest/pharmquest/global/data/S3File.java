package com.pharmquest.pharmquest.global.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

//s3 등록 로직만 확인하기 위해 테스트 용도 db 생성
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "s3_file")
public class S3File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName; // 파일명

    private String fileUrl; // S3 업로드된 파일 URL

    private LocalDateTime uploadedAt; // 업로드 시간

    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
    }
}

