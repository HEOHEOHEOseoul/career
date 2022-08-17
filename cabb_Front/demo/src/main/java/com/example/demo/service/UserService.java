package com.example.demo.service;

import com.example.demo.model.User;

public interface UserService {
	public String getWallet(String email);
	
	public User getUserInfo(String userId);
	public void registerWallet(String email, String wallet, String alias);
}
