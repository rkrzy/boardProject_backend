package com.example.backend.post.dto;

import com.example.backend.common.EventType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequest {
    String title;
    String content;
    Long memberId;
    EventType eventType;
}
