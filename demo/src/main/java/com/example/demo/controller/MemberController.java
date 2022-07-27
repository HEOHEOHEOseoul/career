package com.example.demo.controller;

import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.dto.MemberDTO;

import lombok.RequiredArgsConstructor;

@RequestMapping("/cabb")
@Controller
@RequiredArgsConstructor
public class MemberController {
	//private final MemberService userService;

	// 로그인 실패시
    @GetMapping("/access_denied")
    public String accessDenied() {
        return "access_denied";
    }

    // 회원 가입 진행
    @PostMapping("/signin")
    public String signUp(MemberDTO userVo) {
    	// db에 넣기
        //userService.joinUser(userVo);
        return "redirect:/login";
    }

    // 유저 페이지
    @GetMapping("/user_access")
    public String userAccess(Model model, Authentication authentication) {
        //Authentication 객체를 통해 유저 정보를 가져올 수 있다.
        MemberDTO userVo = (MemberDTO) authentication.getPrincipal();  //userDetail 객체를 가져옴
        return "user_access";
    }
}
