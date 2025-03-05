package com.example.backend.login.Filter;

import com.example.backend.common.Role;
import com.example.backend.login.config.CustomUserDetails;
import com.example.backend.login.jwt.JWTUtil;
import com.example.backend.member.domain.Member;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
     String authorization = request.getHeader("Authorization");

     if(authorization == null || !authorization.startsWith("Bearer ")){
         System.out.println("token null");

         filterChain.doFilter(request,response);

         return;
     }

     String token = authorization.split(" ")[1];

     if(jwtUtil.isExpired(token)){
         System.out.println("token expired");
         filterChain.doFilter(request, response);

         return;
     }
     String loginId = jwtUtil.getLoginId(token);
     String role = jwtUtil.getRole(token);

     Member member = new Member();
     member.setLoginId(loginId);
     member.setPassword("임시 비밀번호");
     member.setRole(Role.valueOf(role));

        CustomUserDetails customUserDetails = new CustomUserDetails(member);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
