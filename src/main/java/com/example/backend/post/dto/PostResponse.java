package com.example.backend.post.dto;

import com.example.backend.member.dto.MemberResponse;

import java.time.LocalDateTime;

public record PostResponse(
    Long id,
    MemberResponse member,
    String title,
    String content,
    String imageUrl,
    int currentMemberCount,
    int memberMax,
    String gift,
    String category,
    LocalDateTime createdAt
){

}
