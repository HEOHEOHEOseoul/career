package com.example.demo.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.model.User;
import com.example.demo.persistence.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userR;
	
	@Override
	public String getWallet(String email) {
		Optional<User> user = userR.findById(email);
		if(user.isEmpty()) return "없는 email입니다.";
		return user.get().getWallet();
	}

	@Override
	public void registerWallet(String email, String wallet, String alias) {
		Optional<User> user = userR.findById(email);
		User temp = null;
		if(user.isPresent()) {
			temp = user.get();
			User saveU = User.builder().alias(alias).id(temp.getId()).role(temp.getRole())
				.wallet(wallet).build();
			userR.save(saveU);
		}
		
	}

	@Override
	public User getUserInfo(String userId) {
		Optional<User> user = userR.findById(userId);
		if (user.isPresent()) {
			return user.get();
		}
		return null;
	}

}
