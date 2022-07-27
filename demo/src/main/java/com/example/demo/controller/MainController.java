package com.example.demo.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@RequestMapping("/cabb")
@Controller
@RequiredArgsConstructor
public class MainController {
	//private final MemberService userService;
	
	// 메인 홈
	@GetMapping("/main")
	public void main() {
		
	}
	
	// 로그인 폼으로 이동 -> 조건에 맞추어 정보 출력
	@GetMapping("login")
	public void login(String error, String logout, Model model, HttpServletRequest request) {
		String referrer = request.getHeader("Referer"); 		
		request.getSession().setAttribute("prevPage", referrer);
				
		if(error != null) {
			model.addAttribute("error", "일치하는 회원정보가 없습니다. 다시 한번 확인해주세요."); 
		}
		if(logout != null) {
			model.addAttribute("logout", "로그아웃 되었습니다. 좋은 하루 되세요."); 
		}
		
	}

    // 회원가입 폼으로 이동
    @GetMapping("/signin")
    public void signUpForm() { }
}
