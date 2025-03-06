package com.example.backend.Image.domain;

import com.example.backend.common.BaseEntity;
import com.example.backend.post.domain.Post;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access =AccessLevel.PROTECTED)
public class Image extends BaseEntity {

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}
