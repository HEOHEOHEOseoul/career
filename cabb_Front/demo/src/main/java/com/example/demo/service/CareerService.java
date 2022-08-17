package com.example.demo.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.example.demo.dto.CareerDTO;
import com.example.demo.dto.PageRequestDTO;
import com.example.demo.dto.PageResponseDTO;
import com.example.demo.model.Tempcareer;

public interface CareerService {
	// 경력 임시 등록
	public Long registerCareer(CareerDTO dto);
	
	// 현재 심사중인 경력 페이지 별로 뽑아오기
	public List<CareerDTO> getTempCareerList(String id);
	public PageResponseDTO getTempCareerList(PageRequestDTO dto);
	
	public CareerDTO getTempCareer(Long num);
	
	public boolean deleteCareer(long num);
	
	public default Tempcareer DtoToEntity(CareerDTO dto) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate cs = LocalDate.parse(dto.getCareer_start(), formatter);
		LocalDate ce = LocalDate.parse(dto.getCareer_end(), formatter);
		Tempcareer entity = Tempcareer.builder().num(dto.getNum()).applier(dto.getApplier()).company(dto.getCompany())
				.career_start(cs).career_end(ce).job(dto.getJob())
				.proof(dto.getProof().getOriginalFilename()).payment("0").build();
		
		return entity;
	}
	
	public default CareerDTO EntityToDto(Tempcareer entity) {
		// proof는 multipartfile이므로 proof_name을 채워서 보내줌
		String cs = entity.getCareer_start().toString();
		String ce = entity.getCareer_end().toString();
		CareerDTO dto = CareerDTO.builder().num(entity.getNum()).applier(entity.getApplier()).company(entity.getCompany())
				.career_start(cs).career_end(ce).job(entity.getJob()).payment(entity.getPayment()).proof_name(entity.getProof()).build();
		
		return dto;
	}
}
