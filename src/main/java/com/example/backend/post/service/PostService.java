package com.example.backend.post.service;

import com.example.backend.Image.domain.Image;
import com.example.backend.Image.repository.ImageRepository;
import com.example.backend.aws.service.AwsS3Service;
import com.example.backend.common.message.Message;
import com.example.backend.member.domain.Member;
import com.example.backend.member.repository.MemberRepository;
import com.example.backend.post.domain.Post;
import com.example.backend.post.dto.PostRequest;
import com.example.backend.post.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@AllArgsConstructor
public class PostService {

    private MessageSource messageSource;
    private final AwsS3Service awsS3Service;
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final MemberRepository memberRepository;

    public ResponseEntity<String> create(PostRequest request, List<MultipartFile> images){

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException(Message.NOT_MEMBER_EXIST.getMessage(messageSource)));


        Post post = new Post (request.getTitle(), request.getContent(), member, request.getEventType());
        postRepository.save(post);

        List<Image> imageList = awsS3Service.uploadFile(images, post);
        imageRepository.saveAll(imageList);

        return ResponseEntity.ok(Message.UPLOAD_SUCCESS.getMessage(messageSource));
    }
}
