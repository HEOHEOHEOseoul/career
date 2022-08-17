package com.example.demo.dto;

import java.util.ArrayList;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TxsDTO {
	
	private ArrayList<String> txid;
	private ArrayList<String> job;
	private ArrayList<String> proof;
	private ArrayList<String> payment;
	private ArrayList<String> company;
	private ArrayList<String> CareerStart;
	private ArrayList<String> CareerEnd;
	
	
	
	
	

}
