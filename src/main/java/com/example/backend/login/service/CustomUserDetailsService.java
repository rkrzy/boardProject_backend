package com.example.backend.login.service;

import com.example.backend.login.config.CustomUserDetails;
import com.example.backend.member.domain.Member;
import com.example.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String memberName) throws UsernameNotFoundException{
        Member member = memberRepository.findByLoginId(memberName);

        if(member == null){
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: "+ memberName);
        }

        return new CustomUserDetails(member);
    }
}
