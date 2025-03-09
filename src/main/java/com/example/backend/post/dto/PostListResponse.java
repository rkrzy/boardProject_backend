    package com.example.backend.post.dto;

    import java.util.List;

    public record PostListResponse (
        List<PostResponse> postList
    ){}