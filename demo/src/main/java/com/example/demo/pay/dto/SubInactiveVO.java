package com.example.demo.pay.dto;

import java.util.Date;

import lombok.Data;

@Data
public class SubInactiveVO {
	private String cid;
	private String sid;
	private String status;
	private Date created_at;
	private Date inactivated_at;
	private Date last_approved_at;
}