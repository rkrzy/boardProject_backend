package com.example.backend.post.controller;

import com.example.backend.Image.repository.ImageRepository;
import com.example.backend.common.EventType;
import com.example.backend.common.message.Message;
import com.example.backend.login.dto.JoinRequest;
import com.example.backend.member.domain.Address;
import com.example.backend.member.domain.Gender;
import com.example.backend.member.repository.MemberRepository;
import com.example.backend.post.domain.Post;
import com.example.backend.post.dto.PostRequest;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static com.example.backend.common.Role.ENTERPRISE;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostService postService; // ✅ Service를 Mocking

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
                .eventType(EventType.MEETING)
                .memberMax(10)
                .build();

        MockMultipartFile postRequestPart = new MockMultipartFile(
                "postRequest",
                "postRequest.json",
                "application/json",
                new ByteArrayInputStream(objectMapper.writeValueAsBytes(postRequest))
        );


        // 3. 이미지 파일 생성
        MockMultipartFile imageFile = new MockMultipartFile(
                "images", // ✅ @RequestPart의 이름과 일치
                "test-image.jpg",
                "image/jpeg",
                "dummy image data".getBytes(StandardCharsets.UTF_8)
        );


        mockMvc.perform(MockMvcRequestBuilders.multipart("/post")
                        .file(postRequestPart)
                        .file(imageFile)
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
                .eventType(EventType.MEETING)
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
                .andExpect((MockMvcResultMatchers.status().isOk()))
                .andExpect(MockMvcResultMatchers.content().string(Message.UPLOAD_SUCCESS.getMessage(messageSource)))
                .andReturn();

        Long postId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/post/{postId}", postId))
                .andExpect((MockMvcResultMatchers.status().isOk()))
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
                .eventType(EventType.MEETING)
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
                .andExpect((MockMvcResultMatchers.status().isOk()))
                .andExpect(MockMvcResultMatchers.content().string(Message.UPLOAD_SUCCESS.getMessage(messageSource)))
                .andReturn();

        Long postId = 2L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/post/{postId}", postId))
                .andExpect((MockMvcResultMatchers.status().isNotFound()))
                .andExpect(MockMvcResultMatchers.content().string(Message.POST_NOT_FOUND.getMessage(messageSource)));

    }
}
