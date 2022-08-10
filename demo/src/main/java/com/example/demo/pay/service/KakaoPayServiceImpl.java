package com.example.demo.pay.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.Tempcareer;
import com.example.demo.pay.dto.KakaoPayApprovalVO;
import com.example.demo.pay.dto.KakaoPayReadyVO;
import com.example.demo.persistence.CareerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoPayServiceImpl implements KakaoPayService {
	private final CareerRepository careerR;
	private final RestTemplate restTemplate;
	
	private KakaoPayReadyVO readyVO;
	
	@Value("${com.choc.kakaopay}")
	private String ADMIN_KEY;
	
	
	@Override
	public String kakaoPayReady(String email, long num) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization","KakaoAK " + ADMIN_KEY);

		headers.add("Accept",MediaType.APPLICATION_JSON_VALUE); 	// 크롬 정책에 따라 무조건 utf-8 형태가 되어, 그 전처럼 사용하면 deprecated
		headers.add("Content-type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=utf-8");
		Optional<Tempcareer> careers = careerR.findById(num);
				
		if (careers.isEmpty() || !careers.get().getApplier().equals(email)) {
			return "/main";
		}
		Tempcareer temp = careers.get();
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("cid", "TC0ONETIME");
		params.add("partner_order_id", Long.toString(num)); 
		params.add("partner_user_id", email);
		params.add("item_name", "증명서 발급");
		params.add("quantity", "1");
		params.add("total_amount", "3000");
		params.add("tax_free_amount", "0");
		params.add("approval_url", "http://localhost:8080/pay/kakaoPaySuccess");
		params.add("cancel_url", "http://localhost:8080/pay/kakaoPayCancel");
		params.add("fail_url", "http://localhost:8080/pay/kakaoPayFail");	
		
		HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<MultiValueMap<String,String>>(params,headers);
		try {
			readyVO = restTemplate.postForObject(new URI("https://kapi.kakao.com/v1/payment/ready"), body, KakaoPayReadyVO.class);
			readyVO.setTemp(temp);
			return readyVO.getNext_redirect_pc_url();
		} catch (RestClientException e) {
			System.out.println("restexcep" + e.getLocalizedMessage());
			e.printStackTrace();
		} catch (URISyntaxException e) {
			System.out.println("urisyn" + e.getLocalizedMessage());
			e.printStackTrace();
		}
		// 위에서 정상적으로 처리 안될시 돌아갈 페이지 처리
		return "/main";
	}

	// 결제 승인 처리 메서드
	@Override
	public KakaoPayApprovalVO kakaoPayApprove(String pg_token) {
		// 헤더 정보
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization","KakaoAK" + ADMIN_KEY);
		headers.add("Accept",MediaType.APPLICATION_JSON_VALUE);
		headers.add("Content-type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=utf-8");
		
		// 바디 파라미터
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("cid", "TC0ONETIME");
		params.add("tid", readyVO.getTid()); // 카카오 페이에서 생성해준 결제 번호
		params.add("partner_order_id", Long.toString(readyVO.getTemp().getNum()));
		params.add("partner_user_id",readyVO.getTemp().getApplier());
		params.add("pg_token", pg_token);
		
		// 헤더와 바디 합치기
		HttpEntity<MultiValueMap<String, String>> body=  new HttpEntity<MultiValueMap<String,String>>(params,headers);
		// 요청
		try {
			KakaoPayApprovalVO approvalVO =
			restTemplate.postForObject(new URI("https://kapi.kakao.com/v1/payment/approve"), body, KakaoPayApprovalVO.class);
			// 결과받아와서 리턴 -> controller에서 redirect: 이동할 주소 리턴
			Tempcareer temp2 = Tempcareer.builder().num(readyVO.getTemp().getNum()).applier(readyVO.getTemp()
					.getApplier()).career_end(readyVO.getTemp().getCareer_end()).career_start(readyVO.getTemp().getCareer_start())
					.company(readyVO.getTemp().getCompany()).job(readyVO.getTemp().getJob()).proof(readyVO.getTemp()
							.getProof()).payment("kakao").build();
			careerR.save(temp2);
			return approvalVO; // 응답으로 받은 정보 리턴
		} catch (RestClientException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
//		crepo.delete(readyVO.getTemp());
		return null;
	}

}
