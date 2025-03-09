package com.example.backend.post.controller;

import com.example.backend.Image.domain.Image;
import com.example.backend.Image.repository.ImageRepository;
import com.example.backend.common.Category;
import com.example.backend.common.message.Message;
import com.example.backend.login.dto.JoinRequest;
import com.example.backend.member.domain.Address;
import com.example.backend.member.domain.Gender;
import com.example.backend.member.repository.MemberRepository;
import com.example.backend.post.domain.Post;
import com.example.backend.post.dto.PostDetailResponse;
import com.example.backend.post.dto.PostListResponse;
import com.example.backend.post.dto.PostRequest;
import com.example.backend.post.dto.PostResponse;
import com.example.backend.post.repository.PostRepository;
import com.example.backend.post.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static com.example.backend.common.Role.ENTERPRISE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostService postService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSource messageSource;

    @BeforeEach
    void setup(){
        JoinRequest joinRequest = JoinRequest.builder()
                .email("testEmain@naver.com")
                .name("tester")
                .loginId("testId")
                .password("password")
                .passwordCheck("password")
                .phoneNumber("testNumber")
                .birthdate(LocalDate.now())
                .address(Address.builder()
                        .country("대한민국")
                        .city("부산")
                        .district("북구")
                        .detailAddress("아파트")
                        .build())
                .role(ENTERPRISE)
                .gender(Gender.MALE)
                .build();
        memberRepository.save(joinRequest.toEntity());
    }
    @AfterEach
    void end(){
        imageRepository.deleteAll();
        postRepository.deleteAll();
    }
    @Test
    @WithMockUser(roles = "ENTERPRISE")
    @DisplayName("게시글 + 이미지 업로드 테스트")
    void createPost() throws Exception {
        PostRequest postRequest = PostRequest.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .memberId(1L)
                .category(Category.MEETING)
                .memberMax(10)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String postRequestJson = objectMapper.writeValueAsString(postRequest);
        MockMultipartFile postRequestPart = new MockMultipartFile(
                "postRequest", "", "application/json", postRequestJson.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile thumbnail = new MockMultipartFile(
                "thumbnail", "thumb.jpg", "image/jpeg", "dummy thumbnail data".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile detail1 = new MockMultipartFile(
                "detailImages", "detail1.jpg", "image/jpeg", "dummy detail1 data".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile detail2 = new MockMultipartFile(
                "detailImages", "detail2.jpg", "image/jpeg", "dummy detail2 data".getBytes(StandardCharsets.UTF_8));



        mockMvc.perform(MockMvcRequestBuilders.multipart("/post")
                        .file(postRequestPart)
                        .file(thumbnail)
                        .file(detail1)
                        .file(detail2)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.content().string(Message.UPLOAD_SUCCESS.getMessage(messageSource)));
    }
    @Test
    @WithMockUser(roles = "ENTERPRISE") //이 주석을 붙이면 token이 없어도 인증된 사용자로 변경된다.
    @DisplayName("게시글 삭제 성공테스트")
    void deletePostSuccess() throws Exception{
        PostRequest postRequest = PostRequest.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .memberId(1L)
                .category(Category.MEETING)
                .build();

        MockMultipartFile postRequestPart = new MockMultipartFile(
                "postRequest",
                "postRequest.json",
                "application/json",
                new ByteArrayInputStream(objectMapper.writeValueAsBytes(postRequest))
        );

        MockMultipartFile imageFile = new MockMultipartFile(
                "images",
                "test-image.jpg",
                "image/jpeg",
                "dummy image data".getBytes(StandardCharsets.UTF_8)
        );

        MvcResult creationResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/post")
                        .file(postRequestPart)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect((status().isOk()))
                .andExpect(MockMvcResultMatchers.content().string(Message.UPLOAD_SUCCESS.getMessage(messageSource)))
                .andReturn();

        Long postId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/post/{postId}", postId))
                .andExpect((status().isOk()))
                .andExpect(MockMvcResultMatchers.content().string(Message.POST_DELETE_SUCCESS.getMessage(messageSource)));

    }

    @Test
    @WithMockUser(roles = "ENTERPRISE") //이 주석을 붙이면 token이 없어도 인증된 사용자로 변경된다.
    @DisplayName("게시글 삭제 실패테스트")
    void deletePostFail() throws Exception{
        PostRequest postRequest = PostRequest.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .memberId(1L)
                .category(Category.MEETING)
                .build();

        MockMultipartFile postRequestPart = new MockMultipartFile(
                "postRequest",
                "postRequest.json",
                "application/json",
                new ByteArrayInputStream(objectMapper.writeValueAsBytes(postRequest))
        );

        MockMultipartFile imageFile = new MockMultipartFile(
                "images",
                "test-image.jpg",
                "image/jpeg",
                "dummy image data".getBytes(StandardCharsets.UTF_8)
        );

        MvcResult creationResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/post")
                        .file(postRequestPart)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect((status().isOk()))
                .andExpect(MockMvcResultMatchers.content().string(Message.UPLOAD_SUCCESS.getMessage(messageSource)))
                .andReturn();

        Long postId = 2L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/post/{postId}", postId))
                .andExpect((status().isNotFound()))
                .andExpect(MockMvcResultMatchers.content().string(Message.POST_NOT_FOUND.getMessage(messageSource)));

    }

    @Test
    @WithMockUser(roles = "ENTERPRISE") //이 주석을 붙이면 token이 없어도 인증된 사용자로 변경된다.
    @DisplayName("게시글 수정 테스트")
    void updatePostSuccess() throws Exception{
        PostRequest postRequest = PostRequest.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .memberId(1L)
                .category(Category.MEETING)
                .build();

        MockMultipartFile postRequestPart = new MockMultipartFile(
                "postRequest",
                "postRequest.json",
                "application/json",
                new ByteArrayInputStream(objectMapper.writeValueAsBytes(postRequest))
        );

        MockMultipartFile imageFile = new MockMultipartFile(
                "images",
                "test-image.jpg",
                "image/jpeg",
                "dummy image data".getBytes(StandardCharsets.UTF_8)
        );

        MvcResult creationResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/post")
                        .file(postRequestPart)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect((status().isOk()))
                .andExpect(MockMvcResultMatchers.content().string(Message.UPLOAD_SUCCESS.getMessage(messageSource)))
                .andReturn();

        Long postId = 1L;
        PostRequest postUpdateRequest = PostRequest.builder()
                .title("테스트 수정 제목")
                .content("테스트 수정 내용")
                .memberId(1L)
                .category(Category.GIFT)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/post/{postId}", postId)
                        .content(objectMapper.writeValueAsBytes(postUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(Message.POST_UPDATE_SUCCESS.getMessage(messageSource)));


    }

    @Test
    @WithMockUser(roles = "ENTERPRISE")
    @DisplayName("게시글 리스트 출력")
    void postList()throws Exception{
//        // 미리 생성한 dummy Member 객체
//        Member member = Member.builder()
//                .name("테스트 회원")
//                .address(Address.builder()
//                        .country("대한민국")
//                        .city("부산")
//                        .district("북구")
//                        .detailAddress("아파트")
//                        .build())
//                .gender(Gender.MALE)
//                .email("test@example.com")
//                .phoneNumber("010-1234-5678")
//                .loginId("testUser")
//                .birthdate(LocalDate.of(1990, 1, 1))
//                .password("password")
//                .role(com.example.backend.common.Role.USER)
//                .build();
//        memberRepository.save(member);
        List<Post> dummyPosts = IntStream.range(0, 10)
                .mapToObj(i -> Post.builder()
                        .title("테스트 제목 " + i)
                        .content("테스트 내용 " + i)
                        .member(memberRepository.findById(1L).get())
                        .category(Category.MEETING)
                        .memberMax(10)
                        .currentAttend(5)
                        .gift("테스트 선물 " + i)
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(dummyPosts);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Post> postPage = new PageImpl<>(dummyPosts, pageable, dummyPosts.size());


        dummyPosts.forEach(post -> {
            Image thumbnailImage = Image.builder()
                    .imageUrl("http://example.com/thumb" + post.getId() + ".jpg")
                    .isThumbnail(true)
                    .post(post)
                    .build();
            imageRepository.save(thumbnailImage);
        });

        ResponseEntity<PostListResponse> responseEntity = postService.getList(null, 0, 10);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        PostListResponse responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        List<PostResponse> postResponses = responseBody.postList();
        assertEquals(10, postResponses.size());

        PostResponse firstDto = postResponses.get(postResponses.size() - 1); // 가장 마지막 요소를 가져오기
        Post firstPost = dummyPosts.get(0);
        assertEquals(firstPost.getId(), firstDto.id());
        assertEquals(firstPost.getMember().getId(), firstDto.id());
        assertEquals(firstPost.getTitle(), firstDto.title());
        assertEquals(firstPost.getContent(), firstDto.content());
        assertEquals("http://example.com/thumb" + firstPost.getId() + ".jpg", firstDto.imageUrl());
        assertEquals(firstPost.getCurrentAttend(), firstDto.currentMemberCount());
        assertEquals(firstPost.getMemberMax(), firstDto.memberMax());
        assertEquals(firstPost.getGift(), firstDto.gift());

        assertEquals(firstPost.getCategory().name(), firstDto.category());

        assertEquals(firstPost.getCreatedAt(), firstDto.createdAt());
    }

    @Test
    @WithMockUser(roles = "ENTERPRISE")
    @DisplayName("게시물 상세 출력")
    void postDetail() throws Exception{
        Post testPost = Post.builder()
                .title("테스트 게시글")
                .content("테스트 내용입니다.")
                .member(memberRepository.findById(1L).get())
                .category(Category.MEETING)
                .memberMax(10)
                .currentAttend(5)
                .gift("테스트 선물")
                .build();
        postRepository.save(testPost);

        Image testImage = Image.builder()
                .imageUrl("http://example.com/test-image.jpg")
                .isThumbnail(true)
                .post(testPost)
                .build();
        imageRepository.save(testImage);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/post/{postId}", testPost.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // HTTP 200 응답 확인
                .andExpect( jsonPath("$.id").value(testPost.getId())) // 게시글 ID 검증
                .andExpect( jsonPath("$.title").value(testPost.getTitle())) // 제목 검증
                .andExpect( jsonPath("$.content").value(testPost.getContent())) // 내용 검증
                .andExpect( jsonPath("$.member.id").value(memberRepository.findById(1L).get().getId())) // 작성자 ID 검증
                .andExpect( jsonPath("$.member.name").value(memberRepository.findById(1L).get().getName())) // 작성자 이름 검증
                .andExpect( jsonPath("$.currentMemberCount").value(testPost.getCurrentAttend())) // 현재 참석자 수 검증
                .andExpect( jsonPath("$.memberMax").value(testPost.getMemberMax())) // 최대 인원 검증
                .andExpect( jsonPath("$.gift").value(testPost.getGift())) // 선물 검증
                .andExpect( jsonPath("$.category").value(testPost.getCategory().name())) // 카테고리 검증
                .andExpect( jsonPath("$.imageUrl[0].imageUrl").value(testImage.getImageUrl())) // 이미지 URL 검증
                .andReturn();

        // JSON 응답을 PostDetailResponse로 변환
        String responseBody = result.getResponse().getContentAsString();
        PostDetailResponse response = objectMapper.readValue(responseBody, PostDetailResponse.class);

        assertNotNull(response);
        assertEquals(testPost.getId(), response.id());
        assertEquals(testPost.getTitle(), response.title());
    }
}
