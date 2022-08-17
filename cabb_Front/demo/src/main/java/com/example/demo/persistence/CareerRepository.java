package com.example.demo.persistence;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Tempcareer;

public interface CareerRepository extends JpaRepository<Tempcareer, Long> {
	// 이메일로 현재 처리중인 경력 확인
	Optional<List<Tempcareer>> findByApplier(String applier);

	
}