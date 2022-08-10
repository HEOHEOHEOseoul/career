package com.example.demo.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.example.demo.dto.CareerDTO;
import com.example.demo.dto.PageRequestDTO;
import com.example.demo.model.Tempcareer;

public interface CareerService {
	// 경력 임시 등록
	public Long registerCareer(CareerDTO dto);
	
	// 현재 심사중인 경력 페이지 별로 뽑아오기
	public List<CareerDTO> getTempCareerList(PageRequestDTO dto);
	
	public default Tempcareer DtoToEntity(CareerDTO dto) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate cs = LocalDate.parse(dto.getCareer_start(), formatter);
		LocalDate ce = LocalDate.parse(dto.getCareer_end(), formatter);
		Tempcareer entity = Tempcareer.builder().num(dto.getNum()).applier(dto.getApplier()).company(dto.getCompany())
				.career_start(cs).career_end(ce).job(dto.getJob())
				.proof(dto.getProof().getName()).payment("0").build();
		
		return entity;
	}
	
	public default CareerDTO EntityToDto(Tempcareer entity) {
		// proof는 impl에서 채움 -> 데이터베이스에서 꺼내서 보여주어야 할 때
		String cs = entity.getCareer_start().toString();
		String ce = entity.getCareer_end().toString();
		CareerDTO dto = CareerDTO.builder().num(entity.getNum()).applier(entity.getApplier()).company(entity.getCompany())
				.career_start(cs).career_end(ce).job(entity.getJob()).payment(entity.getPayment()).build();
		
		return dto;
	}
}
