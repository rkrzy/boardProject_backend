package com.example.backend.login.controller;

import com.example.backend.login.dto.JoinRequest;
import com.example.backend.login.dto.LoginRequest;
import com.example.backend.login.jwt.JWTUtil;
import com.example.backend.login.service.LoginService;
import com.example.backend.member.domain.Address;
import com.example.backend.member.domain.Gender;
import com.example.backend.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class JwtLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private LoginService loginService;

    @Autowired
    private MessageSource messageSource;

    private ObjectMapper objectMapper;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // 추가된 부분

        objectMapper.registerModule(new JavaTimeModule());

        memberRepository.deleteAll();
        if(testInfo.getDisplayName().contains("로그인 성공")||
        testInfo.getDisplayName().contains("로그인 실패")) {
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
    }
    @AfterEach
    void delete() {

    }


    @Test
    @DisplayName("메시지 파일에서 회원가입 성공 메시지 가져오기")
    void testMessageSource() {
        String message = messageSource.getMessage("join.success", null, Locale.KOREAN);
        System.out.println("회원가입 성공 메시지: " + message);
    }

    @Test
    @DisplayName("회원가입 성공 - 200 OK")
    void joinSuccess() throws Exception{

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

        MvcResult mvcResult = mockMvc.perform(post("/jwt-login/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(joinRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        System.out.println("응답 본문: " + content);
    }
    @Test
    @DisplayName("회원가입 실패 - 400 BAD REQUEST")
    void joinFail() throws Exception {

        JoinRequest joinRequest = JoinRequest.builder()
                .email("testEmain@naver.com")
                .name("tester")
                .loginId("testId")
                .password("password")
                .passwordCheck("password")
                .birthdate(LocalDate.now())
                .address(Address.builder()
                        .country("대한민국")
                        .city("부산")
                        .district("북구")
                        .detailAddress("아파트")
                        .build())
                .gender(Gender.MALE)
                .build();

        MvcResult mvcResult = mockMvc.perform(post("/jwt-login/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        System.out.println("응답 본문: " + content);
    }
    @Test
    @DisplayName("회원가입 실패(비밀번호 확인이 일치하지 않을 때) - 400 BAD REQUEST")
    void joinFailPassword() throws Exception{

        JoinRequest joinRequest = JoinRequest.builder()
                .email("testEmain@naver.com")
                .name("tester")
                .loginId("testId")
                .password("password")
                .passwordCheck("password11")
                .birthdate(LocalDate.now())
                .address(Address.builder()
                        .country("대한민국")
                        .city("부산")
                        .district("북구")
                        .detailAddress("아파트")
                        .build())
                .phoneNumber("010-2412")
                .gender(Gender.MALE)
                .build();


        MvcResult mvcResult = mockMvc.perform(post("/jwt-login/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        System.out.println("응답 본문: " + content);
        assertEquals("비밀번호가 동일하지 않습니다." , content);
    }


    @Test
    @DisplayName("로그인 성공 - 200 OK")
    void loginSuccess() throws Exception {
        LoginRequest loginRequest = new LoginRequest("testId", "password");

        MvcResult mvcResult = mockMvc.perform(post("/jwt-login/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.asJsonString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        System.out.println("응답 본문: " + content);



    }
    @Test
    @DisplayName("로그인 실패(회원이 존재하지 않음) - 400 BAD REQUEST")
    void loginFailNotMember() throws Exception {
        LoginRequest loginRequest = new LoginRequest("testId22", "password");

        MvcResult mvcResult = mockMvc.perform(post("/jwt-login/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.asJsonString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        System.out.println("응답 본문: " + content);
    }
    @Test
    @DisplayName("로그인 실패(비밀번호가 다름) - 400 BAD REQUEST")
    void loginFailNotEqualPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest("testId22", "password11");

        MvcResult mvcResult = mockMvc.perform(post("/jwt-login/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.asJsonString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        System.out.println("응답 본문: " + content);
    }


    static class TestUtil {
        private static final com.fasterxml.jackson.databind.ObjectMapper om =
                new com.fasterxml.jackson.databind.ObjectMapper();

        static String asJsonString(final Object obj) {
            try {
                return om.writeValueAsString(obj);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}