package com.example.demo.service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.CareerDTO;
import com.example.demo.dto.PageRequestDTO;
import com.example.demo.dto.PageResponseDTO;
import com.example.demo.model.Tempcareer;
import com.example.demo.persistence.CareerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CareerServiceImpl implements CareerService{
	private final CareerRepository careerR;
	
	@Value("${com.choc.upload.path}")
	private String uploadPath;
	
	private void makeFolder(String email) {
		File uploadPathDir = new File(uploadPath, email);
		if(uploadPathDir.exists() == false) {
			uploadPathDir.mkdirs();
		}
	}
	
	@Override
	public Long registerCareer(CareerDTO dto) {
		Tempcareer career = DtoToEntity(dto);
		Optional<List<Tempcareer>> ttemp = careerR.findByApplier(dto.getApplier());
		if(ttemp.isPresent()) {
			for(Tempcareer tt : ttemp.get()) {
				if(dto.getCareer_start() == tt.getCareer_start().toString() && tt.getCareer_end().toString() == dto.getCareer_end() && dto.getCompany() == tt.getCompany()) return null;
			}
		}
		
		MultipartFile uploadFile = dto.getProof();
		Tempcareer t = null;
		
		if(uploadFile.isEmpty() == false) {
			//이미지 파일 또는 pdf파일만 업로드 -> 아니면 작업 중단
			String filetype = uploadFile.getContentType();
			if(filetype.startsWith("image") == false && !filetype.equals("application/pdf")) {
				return null;
			}
			//원본 파일의 파일 이름 찾아오기
			String originalName = uploadFile.getOriginalFilename();
			String fileName = originalName.substring(originalName.lastIndexOf("\\") + 1);
			
			makeFolder(dto.getApplier());
			
			//업로드 할 파일의 경로를 생성
			String uuid = UUID.randomUUID().toString(); //파일 이름의 중복을 피하기 위해서 생성
			String saveName = uploadPath + File.separator + uuid + fileName;
			Path savePath = Paths.get(saveName);
			
			try {
				//파일 업로드
				uploadFile.transferTo(savePath);
				t = Tempcareer.builder().applier(career.getApplier()).career_end(career.getCareer_end())
						.career_start(career.getCareer_start()).company(career.getCompany()).job(career.getJob()).payment(career.getPayment())
						.proof(uuid + fileName).build();
			}catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		Tempcareer result = careerR.save(t);
		return result.getNum();
	}

	@Override
	public List<CareerDTO> getTempCareerList(String id) {
		Sort sort = Sort.by("num").descending();
		Optional<List<Tempcareer>> pages = careerR.findByApplier(id);
		List<CareerDTO> list = new ArrayList<>();
		int i = 0; 
		for(Tempcareer obj: pages.get()) {
			if(i == 10) break;
			String parsedcs = obj.getCareer_start().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			String parsedce = obj.getCareer_end().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			CareerDTO cDTO = CareerDTO.builder().num(obj.getNum()).applier(obj.getApplier()).career_start(parsedcs)
					.career_end(parsedce).company(obj.getCompany()).job(obj.getJob()).payment(obj.getPayment())
					.build();
			list.add(cDTO);
			i++;
		}
		return list;
	}
	
	@Override
	public PageResponseDTO getTempCareerList(PageRequestDTO dto) {
		Sort sort = Sort.by("num").descending();
		Pageable pageable = PageRequest.of(dto.getPage()-1, dto.getSize(), sort);
		Page<Tempcareer> pages = careerR.findAll(pageable);
		PageResponseDTO result = new PageResponseDTO();
		result.setTotalPage(pages.getTotalPages());
		result.makePageList(pageable);
		
		List<CareerDTO> list = new ArrayList<>();
		for(Tempcareer obj: pages.getContent()) {
			String parsedcs = obj.getCareer_start().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			String parsedce = obj.getCareer_end().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			CareerDTO cDTO = CareerDTO.builder().num(obj.getNum()).applier(obj.getApplier()).career_start(parsedcs)
					.career_end(parsedce).company(obj.getCompany()).job(obj.getJob()).payment(obj.getPayment())
					.build();
			list.add(cDTO);
		}
		result.setList(list);
		
		return result;
	}
	
	
	@Override
	public CareerDTO getTempCareer(Long num) {
		Optional<Tempcareer> temp = careerR.findById(num);
		if(temp.isEmpty()) {
			return null;
		}
		return EntityToDto(temp.get());
	}

	@Override
	public boolean deleteCareer(long num) {
		careerR.deleteById(num);
		
		Optional<Tempcareer> temp = careerR.findById(num);
		if(temp.isEmpty()) {
			return true;
		}
		
		return false;
	}



}
