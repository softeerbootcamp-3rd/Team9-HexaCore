package com.hexacore.tayo.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class S3Manager {

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /* 이미지 파일을 S3에 업로드하고 URL 반환 */
    public String uploadImage(MultipartFile image) {
        String originalFilename = image.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.getSize());
        metadata.setContentType(image.getContentType());

        try {
            amazonS3Client.putObject(
                    new PutObjectRequest(bucket, originalFilename, image.getInputStream(), metadata)
            );
        } catch (IOException e) {
            throw new GeneralException(ErrorCode.S3_UPLOAD_FAILED);
        }

        return amazonS3Client.getUrl(bucket, originalFilename).toString();
    }

    /* 이미지 파일을 S3에서 삭제 */
    public void deleteImage(String imageUrl) {
        // 이미지 url 로부터 S3에서 이미지를 식별하는 키 값을 추출
        String[] urlParts = imageUrl.split("/");
        String key = String.join("/", Arrays.copyOfRange(urlParts, 3, urlParts.length));

        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, key));
    }
}
