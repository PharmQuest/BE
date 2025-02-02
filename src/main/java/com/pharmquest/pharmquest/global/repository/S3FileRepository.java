package com.pharmquest.pharmquest.global.repository;

import com.pharmquest.pharmquest.global.data.S3File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//s3file db(테스트 용도) 활용 목적
@Repository
public interface S3FileRepository extends JpaRepository<S3File, Long> {
}