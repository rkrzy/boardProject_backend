package com.example.backend.post.controller;


import com.example.backend.post.dto.PostRequest;
import com.example.backend.post.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<String> createPost(
            @RequestPart("postRequest") PostRequest request,
            @RequestPart(value = "images", required = false)List<MultipartFile> images) {

        return postService.create(request, images);
    }
}
