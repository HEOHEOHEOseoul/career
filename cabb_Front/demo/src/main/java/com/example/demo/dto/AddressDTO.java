package com.example.demo.dto;

import lombok.Data;

@Data
public class AddressDTO {
	public AddressDTO(String address) {
		this.Address = address;
	}
	private String Address;
}
