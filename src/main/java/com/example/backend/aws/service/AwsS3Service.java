package com.example.backend.aws.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.backend.Image.domain.Image;
import com.example.backend.Image.repository.ImageRepository;
import com.example.backend.post.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AwsS3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    public List<Image> uploadFiles(Post post, MultipartFile thumbnail, List<MultipartFile> detailImages) {
        List<Image> uploadedImages = new ArrayList<>();

        // 썸네일 처리 (방법 1과 동일)
        if (thumbnail != null && !thumbnail.isEmpty()) {
            String thumbnailFileName = createFileName(thumbnail.getOriginalFilename());
            ObjectMetadata thumbnailMeta = new ObjectMetadata();
            thumbnailMeta.setContentLength(thumbnail.getSize());
            thumbnailMeta.setContentType(thumbnail.getContentType());

            try (InputStream thumbnailStream = thumbnail.getInputStream()) {
                amazonS3.putObject(new PutObjectRequest(bucket, thumbnailFileName, thumbnailStream, thumbnailMeta)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "썸네일 업로드 실패");
            }
            Image thumbnailImage = new Image(thumbnailFileName, true, post);
            uploadedImages.add(thumbnailImage);
        }

        // 상세 이미지 처리 (List 사용)
        if (detailImages != null && !detailImages.isEmpty()) {
            detailImages.forEach(file -> {
                if (file != null && !file.isEmpty()) {
                    String fileName = createFileName(file.getOriginalFilename());
                    ObjectMetadata meta = new ObjectMetadata();
                    meta.setContentLength(file.getSize());
                    meta.setContentType(file.getContentType());

                    try (InputStream inputStream = file.getInputStream()) {
                        amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, meta)
                                .withCannedAcl(CannedAccessControlList.PublicRead));
                    } catch (IOException e) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "상세 이미지 업로드 실패");
                    }
                    Image detailImage = new Image(fileName, false, post);
                    uploadedImages.add(detailImage);
                }
            });
        }
        return uploadedImages;
    }

    // 파일명을 난수화하기 위해 UUID 를 활용하여 난수를 돌린다.
    public String createFileName(String fileName){
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    //  "."의 존재 유무만 판단
    private String getFileExtension(String fileName){
        try{
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일" + fileName + ") 입니다.");
        }
    }


    public void deleteFile(String fileName){
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
        System.out.println(bucket);
    }
}