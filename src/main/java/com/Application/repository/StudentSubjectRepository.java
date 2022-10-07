package com.Application.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Application.domain.StudentSubject;

@Repository
public interface StudentSubjectRepository extends JpaRepository<StudentSubject, Long>{

	List<StudentSubject> findBySubject_id(Long id);
	
	Page<StudentSubject> findBySubject_id(Long id, Pageable pageable);
	
	List<StudentSubject> findByUser_id(Long id);
	
	Page<StudentSubject> findByUser_id(Long id, Pageable pageable);
	
	StudentSubject findByUser_idAndSubject_id(Long user_id, Long subject_id); 
}
