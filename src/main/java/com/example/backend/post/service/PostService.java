package com.example.backend.post.service;

import com.example.backend.Image.domain.Image;
import com.example.backend.Image.repository.ImageRepository;
import com.example.backend.aws.service.AwsS3Service;
import com.example.backend.common.message.Message;
import com.example.backend.member.domain.Member;
import com.example.backend.member.repository.MemberRepository;
import com.example.backend.post.domain.Post;
import com.example.backend.post.dto.PostListResponse;
import com.example.backend.post.dto.PostRequest;
import com.example.backend.post.dto.PostResponse;
import com.example.backend.post.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class PostService {

    private MessageSource messageSource;
    private final AwsS3Service awsS3Service;
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final MemberRepository memberRepository;


    public ResponseEntity<String> create(PostRequest request,MultipartFile thumbnail, List<MultipartFile> images){

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException(Message.NOT_MEMBER_EXIST.getMessage(messageSource)));

        Post post = null;
        switch (request.getEventType()){
            case MEETING :
                post = new Post (request.getTitle(), request.getContent(), member, request.getEventType(), request.getMemberMax(), null);
                break;
            case SALE, GIFT :
                post = new Post (request.getTitle(), request.getContent(), member, request.getEventType(), 0,request.getGift());
                break;
            default:
                throw new IllegalArgumentException("지원하지 않는 게시물 타입입니다: " + request.getEventType());
        }

        postRepository.save(post);

        List<Image> imageList = awsS3Service.uploadFiles(post, thumbnail, images);
        imageRepository.saveAll(imageList);

        return ResponseEntity.ok(Message.UPLOAD_SUCCESS.getMessage(messageSource));
    }

    public ResponseEntity<String> delete(Long postId){

        Optional<Post> post = postRepository.findById(postId);
        if(post.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message.POST_NOT_FOUND.getMessage(messageSource));
        }

        imageRepository.deleteAllByPost(post.get());
        postRepository.delete(post.get());

        return ResponseEntity.ok(Message.POST_DELETE_SUCCESS.getMessage(messageSource));
    }

    @Transactional
    public ResponseEntity<String> update(PostRequest request, Long postId){
        Optional<Post> post = postRepository.findById(postId);
        if(post.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message.POST_NOT_FOUND.getMessage(messageSource));
        }
        Post updatePost = post.get();

        updatePost.setContent(request.getContent());
        updatePost.setTitle(request.getTitle());
        updatePost.setMemberMax(request.getMemberMax());
        updatePost.setEventType(request.getEventType());

        return ResponseEntity.ok(Message.POST_UPDATE_SUCCESS.getMessage(messageSource));
    }
    public ResponseEntity<PostListResponse> getList(String category, int page, int size){
        Pageable pageable = (Pageable) PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Post> posts;
        if(category == null || category.isEmpty()){
            posts = postRepository.findAll(pageable);
        }
        else{
            posts = postRepository.findByCategory(category, pageable);
        }
        List<PostResponse> postResponseList = posts.getContent().
                stream()
                .map(post -> {
                    List<Image> images = imageRepository.findByPostId(post.getId());
                })
                .toList();

        PostListResponse response = new PostListResponse(postResponseList);

        return ResponseEntity.ok(response);
    }

}
