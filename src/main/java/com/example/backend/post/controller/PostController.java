package com.example.backend.post.controller;


import com.example.backend.post.dto.PostRequest;
import com.example.backend.post.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
            @RequestPart(value = "thumbnail") MultipartFile thumbnail,
            @RequestPart(value = "images", required = false)List<MultipartFile> images) {

        return postService.create(request, thumbnail, images);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId){
        return postService.delete(postId);
    }
    @PutMapping("/{postId}")
    public ResponseEntity<String> updatePost(@RequestBody PostRequest request,@PathVariable Long postId){
        return postService.update(request,postId);
    }

    @GetMapping
    public ResponseEntity listPost(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
            ){
            return postService.getList(category,page,size);
    }
    @GetMapping("{postId}")
    public ResponseEntity detailPost(@PathVariable Long postId){

        return postService.detail(postId);
    }
}
