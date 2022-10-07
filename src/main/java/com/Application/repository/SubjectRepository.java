package com.Application.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Application.domain.Subject;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
	
	public List<Subject> findByUser_id(Long id);
	
	public Page<Subject> findByUser_id(Long id, Pageable pageable);
	
}
