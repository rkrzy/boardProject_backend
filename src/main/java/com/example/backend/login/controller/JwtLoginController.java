package com.example.backend.login.controller;

import com.example.backend.login.dto.JoinRequest;
import com.example.backend.login.dto.LoginRequest;
import com.example.backend.login.jwt.JWTUtil;
import com.example.backend.login.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jwt-login")
public class JwtLoginController {

    private final LoginService loginService;
    private final JWTUtil jwtUtil;

    @PostMapping("/join")
    public ResponseEntity<String> join(@Valid @RequestBody JoinRequest request){
        ResponseEntity<String> response = loginService.memberJoin(request);

        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request){
        ResponseEntity<String> response = loginService.login(request);

        return response;
    }
}
