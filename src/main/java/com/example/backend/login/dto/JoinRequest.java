package com.example.backend.login.dto;

import com.example.backend.common.Role;
import com.example.backend.member.domain.Address;
import com.example.backend.member.domain.Gender;
import com.example.backend.member.domain.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinRequest {


    @NotBlank(message = "ID를 입력하세요.")
    private String loginId;

    @NotBlank(message = "비밀번호를 입력하세요.")
    private String password;
    private String passwordCheck;

    @NotBlank(message = "이름을 입력하세요.")
    private String name;

    @NotNull(message = "주소를 입력하세요.")
    private Address address;

    @NotNull(message = "성별을 입력하세요.")
    private Gender gender;

    @NotBlank(message = "이메일을 입력하세요.")
    private String email;

    @NotNull(message = "생일을 입력하세요")
    private LocalDate birthdate;

    @Null(message = "역할을 입력하세요")
    private Role role;

    @NotBlank(message = "전화번호를 입력하세요")
    private String phoneNumber;
    public Member toEntity(){
        return Member.builder()
                .loginId(this.loginId)
                .password(this.password)
                .name(this.name)
                .role(Role.USER)
                .address(this.address)
                .phoneNumber(this.phoneNumber)
                .gender(this.gender)
                .email(this.email)
                .role(this.role)
                .birthdate(this.birthdate)
                .build();
    }


}
