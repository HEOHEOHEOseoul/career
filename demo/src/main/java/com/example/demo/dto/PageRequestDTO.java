package com.example.demo.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class PageRequestDTO {
	//페이지 번호 - 1부터 시작
	private int page;
	//한 페이지에 보여질 데이터 개수
	private int size;
	
	// 자신의 아이디 미리 받아둠
	private String id;
	
	//생성자
	public PageRequestDTO() {
		page = 1;
		size = 10;
	}
	
	//페이지 번호 와 데이터 개수를 가지고 Pageable 객체를 생성해주는 메서드
	public Pageable getPageable(Sort sort) {
		//JPA에서는 페이지 번호가 0부터 시작하기 때문에 -1
		return PageRequest.of(page-1, size, sort);
	}
}