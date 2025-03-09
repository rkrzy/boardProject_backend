package com.example.backend.post.domain;

import com.example.backend.common.BaseEntity;
import com.example.backend.common.Category;
import com.example.backend.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = true)
    private Integer memberMax;

    @Column(nullable = true)
    private Integer currentAttend;

    @Column(nullable = true)
    private String gift;
}
