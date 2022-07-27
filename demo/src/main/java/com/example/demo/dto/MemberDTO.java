package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberDTO {
	private String id;
	private String pw;
	private String link;
	private String wallet;
	private String auth;
}