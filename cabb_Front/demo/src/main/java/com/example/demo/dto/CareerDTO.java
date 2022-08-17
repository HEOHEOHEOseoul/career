package com.example.demo.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CareerDTO {
	private Long num;
	private String applier;
	private String company;
	private String career_start;
	private String career_end;
	private String job;
	private MultipartFile proof;
	private String payment;
	private String proof_name;
}
