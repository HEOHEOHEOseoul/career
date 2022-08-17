package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.config.LoginUser;
import com.example.demo.dto.TxDTO;
import com.example.demo.security.dto.SessionUser;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class WalletRestController {
	private final UserService userS;
	@GetMapping("/find")
	public ResponseEntity<?> getItem(String email){
		Map<String, String> result = new HashMap<>();
		try {
			result.put("wallet", userS.getWallet(email));
		}catch(Exception e) {
			result.put("wallet", "error");
		}
		return ResponseEntity.ok().body(result);
	}
		
	// 관리자가 승인시, 사용자 세션 정보에서 지갑 주소 받아올 것
}
