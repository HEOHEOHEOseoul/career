package com.example.demo.security;

import java.util.Collections;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.demo.model.User;
import com.example.demo.persistence.CareerRepository;
import com.example.demo.persistence.UserRepository;
import com.example.demo.security.dto.OAuthAttributes;
import com.example.demo.security.dto.SessionUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User>{
	private final UserRepository userRepository;
	private final HttpSession httpSession;
	
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		
		OAuth2User oAuth2User = delegate.loadUser(userRequest);
		
		// 로그인 진행 중인 서비스를 구분하기 위한 코드 -> 나중에 구글 외에 것 추가할 때 필요 
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		
		// OAuth2 로그인 진행시 키가 되는 필드값 = PK와 맥락이 같음. 구글은 지원 O, 네이버, 카카오 지원 X 구글은 기본 코드가 sub.
		String userNameAttributename = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
		
		// OAuth2User의 attribute를 담기 위한 클래스.
		OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributename, oAuth2User.getAttributes());
		
		User user = saveOrUpdate(attributes);
		
		httpSession.setAttribute("user", new SessionUser(user));
		
		return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())), attributes.getAttributes(), attributes.getNameAttributeKey());
	}
	
	private User saveOrUpdate(OAuthAttributes attributes) {
		User user = userRepository.findById(attributes.getEmail()).orElse(attributes.toEntity());
		
		return userRepository.save(user);
	}
}
