package com.example.demo.service;

public interface UserService {
	public String getWallet(String email);
	
	public void registerWallet(String email, String wallet, String alias);
}
