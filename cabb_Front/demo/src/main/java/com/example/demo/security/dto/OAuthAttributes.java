package com.example.demo.security.dto;

import java.util.Map;

import com.example.demo.model.Role;
import com.example.demo.model.User;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthAttributes {
	private Map<String, Object> attributes;
	private String nameAttributeKey;
	private String email;
	
	@Builder
	public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String email, String wallet, String alias) {
		this.attributes = attributes;
		this.nameAttributeKey = nameAttributeKey;
		this.email = email;
	}
	
	public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
		if("naver".equals(registrationId)) {
			return ofNaver("id", attributes);
		}
		return ofGoogle(userNameAttributeName, attributes);
	}
	
	public static OAuthAttributes ofGoogle(String userNameAttributeKey, Map<String, Object> attributes) {
		return OAuthAttributes.builder().email((String)attributes.get("email")).attributes(attributes)
				.nameAttributeKey(userNameAttributeKey).build();
	}
	
	public static OAuthAttributes ofNaver(String userNameAttributeKey, Map<String, Object> attributes) {
		Map<String, Object> response =(Map<String, Object>)attributes.get("response"); 
		return OAuthAttributes.builder().email((String)response.get("email")).attributes(response)
				.nameAttributeKey(userNameAttributeKey).build();
	}
	public User toEntity() {
		return User.builder().id(email).role(Role.USER).build();
	}
}
