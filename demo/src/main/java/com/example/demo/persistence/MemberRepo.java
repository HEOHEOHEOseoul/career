package com.example.demo.persistence;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Member;

public interface MemberRepo extends JpaRepository<Member, String> {
}