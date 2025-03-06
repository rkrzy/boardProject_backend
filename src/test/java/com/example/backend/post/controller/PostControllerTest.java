package com.example.backend.post.controller;

import com.example.backend.common.EventType;
import com.example.backend.common.message.Message;
import com.example.backend.login.dto.JoinRequest;
import com.example.backend.member.domain.Address;
import com.example.backend.member.domain.Gender;
import com.example.backend.member.repository.MemberRepository;
import com.example.backend.post.dto.PostRequest;
import com.example.backend.post.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

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
                .gender(Gender.MALE)
                .build();
        memberRepository.save(joinRequest.toEntity());
    }
    @Test
    @DisplayName("게시글 + 이미지 업로드 테스트")
    void createPost() throws Exception {
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
}
