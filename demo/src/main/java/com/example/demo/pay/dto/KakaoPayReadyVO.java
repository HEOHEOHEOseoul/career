package com.example.demo.pay.dto;

import java.util.Date;

import com.example.demo.dto.CareerDTO;
import com.example.demo.model.Tempcareer;

import lombok.Data;

@Data
public class KakaoPayReadyVO {
	private String tid;
	private String next_redirect_app_url; 
	private String next_redirect_mobile_url;
	private String next_redirect_pc_url;
	private Date created_at;
	
	private Tempcareer temp;
}