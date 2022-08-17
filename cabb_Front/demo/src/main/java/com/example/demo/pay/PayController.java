package com.example.demo.pay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.pay.dto.KakaoPayApprovalVO;
import com.example.demo.pay.service.KakaoPayService;

@Controller
@RequestMapping("/pay")
public class PayController {
	@Autowired
	private KakaoPayService service;
	
	@GetMapping("/kakaoPay")
	public String kakaoPay(@RequestParam("email")String email, @RequestParam("num")long num) {
		String responseURL = service.kakaoPayReady(email, num);
		return "redirect:" + responseURL; // 주소연결
	}
	
	// 준비 요청성공시 실행될 요청 메서드
	// 파라미터로 넘어오는 pg_token 받고
	@GetMapping("/kakaoPaySuccess")
	public void kakaoPaySuccess(@RequestParam("pg_token")String pg_token, Model model) {
		KakaoPayApprovalVO orderVO = service.kakaoPayApprove(pg_token);
		
		model.addAttribute("num",orderVO.getPartner_order_id());
	}
	
}
