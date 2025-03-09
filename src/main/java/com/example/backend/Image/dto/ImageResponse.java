package com.example.backend.Image.dto;

public record ImageResponse(
        Long id,
        String imageUrl,
        boolean isThumbnail
) {
}
