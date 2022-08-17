package com.example.demo.dto;

import lombok.Data;

@Data
public class NewWalletDTO {
	private String address;
	private String alias;
	private String privateKey;
	private String publickey;
}
