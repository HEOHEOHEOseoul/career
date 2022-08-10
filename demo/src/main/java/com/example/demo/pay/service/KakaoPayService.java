package com.example.demo.pay.service;

import com.example.demo.pay.dto.KakaoPayApprovalVO;

public interface KakaoPayService {
	public String kakaoPayReady(String email, long num);
		
	public KakaoPayApprovalVO kakaoPayApprove(String pg_token);

}