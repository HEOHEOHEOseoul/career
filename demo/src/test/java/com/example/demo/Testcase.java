package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.model.Member;
import com.example.demo.persistence.MemberRepo;

@SpringBootTest
public class Testcase {
@Autowired
private MemberRepo mrepo;

	//@Test
	public void adminM() {
		Member m = Member.builder().id("admin@test.com").pw(new BCryptPasswordEncoder().encode("1234")).auth("admin").build();
		mrepo.save(m);
	}
	
	//@Test
	public void userM() {
		Member m = Member.builder().id("user@test.com").pw(new BCryptPasswordEncoder().encode("1234")).auth("user").build();
		mrepo.save(m);
	}
}
