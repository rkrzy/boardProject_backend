package com.example.backend.common.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@Getter
@RequiredArgsConstructor
public enum Message {
    // 로그인 관련
    NOT_MEMBER_EXIST("login.not_exist"),
    NOT_EQUAL_PASSWORD("login.password_wrong"),

    // 회원가입 관련
    ALREADY_MEMBER_EXIST("join.already_exist"),
    NOT_EQUAL_CHECK_PASSWORD("join.password_not_match"),

    // 성공 메시지
    JOIN_SUCCESS("join.success"),

    //게시물 작성 성공 메시지
    UPLOAD_SUCCESS("post.success");

    private final String code; // messages.properties의 키 값

    public String getMessage(MessageSource messageSource) {
        System.out.println(LocaleContextHolder.getLocale());
        return messageSource.getMessage(this.code, null, LocaleContextHolder.getLocale());
    }
}
