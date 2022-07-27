package com.example.demo.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	//private final CustomOAuth2UserService cOAth;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception{
		http.csrf().disable().headers().frameOptions().disable().and()
			.authorizeRequests().antMatchers("/","/css/**","/images/**","");
	}
}
