package com.example.demo.dto;

import lombok.Data;

@Data
public class AliasDTO {
	public AliasDTO(String alias2) {
		this.Alias = alias2;
	}

	private String Alias;
}
