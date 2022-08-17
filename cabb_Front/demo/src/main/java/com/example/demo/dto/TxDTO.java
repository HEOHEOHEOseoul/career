package com.example.demo.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TxDTO {
	private String Txid;
	private String CareerStart;
	private String CareerEnd;
	private String Job;
	private String Proof;
	private String Payment;
	private String Company;
	private String Sign;
	private String Hash;
}


