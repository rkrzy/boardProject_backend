package com.example.backend.login.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JWTUtil {
    private SecretKey secretKey;

    public JWTUtil(@Value("{$spring.jwt.secret}") String secret) {
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public String getLoginId(String token) {

        Jws<Claims> jwsClaims = Jwts.parserBuilder()
                .setSigningKey(secretKey)  // secretKey는 서명 검증에 사용될 SecretKey 객체입니다.
                .build()
                .parseClaimsJws(token);      // token은 검증할 JWT 문자열
        return jwsClaims.getBody().get("loginId", String.class);

    }
    public String getRole(String token) {
        Jws<Claims> jwsClaims = Jwts.parserBuilder()
                .setSigningKey(secretKey)  // secretKey는 서명 검증에 사용될 SecretKey 객체입니다.
                .build()
                .parseClaimsJws(token);      // token은 검증할 JWT 문자열
        return jwsClaims.getBody().get("role", String.class);
    }
    public boolean isExpired(String token) {
        Jws<Claims> jwsClaims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
        return jwsClaims.getBody().getExpiration().before(new Date());
    }

    public String createJwt(String loginId, String role, Long expiredMs){
        return Jwts.builder()
                .claim("loginId", loginId)
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis()))//토큰 현재 발행 시간 설정
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))//토큰 소멸 시간 설정
                .signWith(secretKey, SignatureAlgorithm.HS256)//주입한 secret key를 통해서 암호화 진행
                .compact();//토큰을 compact해서 리턴
    }
}
