package com.example.backend.Image.repository;

import com.example.backend.Image.domain.Image;
import com.example.backend.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    public List<Image> findAllByPost(Post post);


    public int deleteAllByPost(Post post);
}
