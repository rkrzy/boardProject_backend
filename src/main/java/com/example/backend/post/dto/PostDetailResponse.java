package com.example.backend.post.dto;

import com.example.backend.Image.dto.ImageResponse;
import com.example.backend.member.dto.MemberResponse;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetailResponse(
        Long id,
        MemberResponse member,
        String title,
        String content,
        List<ImageResponse> imageUrl,
        int currentMemberCount,
        int memberMax,
        String gift,
        String category,
        LocalDateTime createdAt
) {
}
