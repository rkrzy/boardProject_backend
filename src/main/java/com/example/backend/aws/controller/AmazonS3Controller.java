package com.example.backend.aws.controller;


import com.example.backend.aws.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class AmazonS3Controller {

    private final AwsS3Service awsS3Service;

//    @PostMapping
//    public ResponseEntity<List<String>> uploadFile(@RequestParam("multipartFile") List<MultipartFile> multipartFiles){
//        return ResponseEntity.ok(awsS3Service.uploadFile(multipartFiles));
//    }

    @DeleteMapping
    public ResponseEntity<String> deleteFile(String fileName){
        awsS3Service.deleteFile(fileName);
        return ResponseEntity.ok(fileName);
    }

}