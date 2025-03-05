package com.example.backend.login.service;

import com.example.backend.common.message.Message;
import com.example.backend.login.dto.JoinRequest;
import com.example.backend.login.dto.LoginRequest;
import com.example.backend.login.jwt.JWTUtil;
import com.example.backend.member.domain.Member;
import com.example.backend.member.repository.MemberRepository;
import com.example.backend.member.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;

import java.util.Locale;

import static com.example.backend.common.CommonElement.*;

@Service
@AllArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private MessageSource messageSource;

    public String getMessage(String code) {
        return messageSource.getMessage(code, null, Locale.getDefault());
    }

    public ResponseEntity<String> login(LoginRequest loginRequest){

        Member member = memberRepository.findByLoginId(loginRequest.getLoginId());

        if(member == null) {
            return ResponseEntity.badRequest().body(Message.NOT_MEMBER_EXIST.getMessage(messageSource));
        }

        if(bCryptPasswordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            return ResponseEntity.badRequest().body(Message.NOT_EQUAL_PASSWORD.getMessage(messageSource));
        }

        String token = jwtUtil.createJwt(member.getLoginId(), member.getRole().name(), JWT_EXPIRATION_PERIOD);

        return ResponseEntity.ok(token);
    }


    public ResponseEntity<String> memberJoin(JoinRequest joinRequest){

        if(memberRepository.existsByLoginId(joinRequest.getLoginId())){
            return ResponseEntity.badRequest().body(Message.ALREADY_MEMBER_EXIST.getMessage(messageSource));
        }
        if(!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())){
            return ResponseEntity.badRequest().body(Message.NOT_EQUAL_CHECK_PASSWORD.getMessage(messageSource));
        }

        joinRequest.setPassword(bCryptPasswordEncoder.encode(joinRequest.getPassword()));

        memberRepository.save(joinRequest.toEntity());

        return ResponseEntity.ok(Message.JOIN_SUCCESS.getMessage(messageSource));
    }
}
