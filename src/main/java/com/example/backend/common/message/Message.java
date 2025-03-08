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

    //게시물
    UPLOAD_SUCCESS("post.success"),//게시물 작성 성공 메시지
    UPLOAD_FAIL("post.fail"),//게시물 작성 실패 메시지

    POST_DELETE_SUCCESS("post.delete_success"),//게시물 삭제 성공 메시지
    POST_DELETE_FAIL("post.delete_fail"),//게시물 삭제 실패 메시지
    POST_NOT_FOUND("post.not_found"),//게시물 없을때

    POST_UPDATE_SUCCESS("post.update_success"),
    POST_UPDATE_FAIL("post.update_fail");


    private final String code; // messages.properties의 키 값

    public String getMessage(MessageSource messageSource) {
        System.out.println(LocaleContextHolder.getLocale());
        return messageSource.getMessage(this.code, null, LocaleContextHolder.getLocale());
    }
}
