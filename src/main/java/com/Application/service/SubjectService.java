package com.Application.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.Application.domain.StudentSubject;
import com.Application.domain.Subject;
import com.Application.domain.User;
import com.Application.repository.StudentSubjectRepository;
import com.Application.repository.SubjectRepository;
import com.Application.repository.UserRepository;

@Service
public class SubjectService {

	@Autowired
	SubjectRepository subjectRepository;
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	StudentSubjectRepository studentSubjectRepository;

	public Subject saveSubject(Subject subject) {

		return subjectRepository.save(subject);
	}

	public List<Subject> getAllSubjects() {

		return subjectRepository.findAll();
	}

	public Subject getSubject(Long id) {

		return subjectRepository.findById(id).get();
	}

	public void deleteSubjectbyId(Long id) {

		subjectRepository.deleteById(id);
	}

	public boolean checkEnrollment(Long subject_id, User user) {
		
		if (studentSubjectRepository.findByUser_idAndSubject_id(user.getId(), subject_id) != null) {
			return true;
		}	
		return false;
	}

	public void enroll(User user, Long subject_id) {

		StudentSubject data = studentSubjectRepository.findByUser_idAndSubject_id(user.getId(), subject_id);

		if (data != null) {

			studentSubjectRepository.delete(data);
		} else {

			data = new StudentSubject(user, subjectRepository.findById(subject_id).get());
			studentSubjectRepository.save(data);
		}
	}
	
	public void changeStatus(User user, Long subject_id) {

		StudentSubject data = studentSubjectRepository.findByUser_idAndSubject_id(user.getId(), subject_id);

		if (data != null) {

			studentSubjectRepository.delete(data);
		} else {

			data = new StudentSubject(user, subjectRepository.findById(subject_id).get());
			studentSubjectRepository.save(data);
		}
	}

	public void unenrollStudent(Long student_id, Long subject_id) {

		StudentSubject data = studentSubjectRepository.findByUser_idAndSubject_id(student_id, subject_id);

		studentSubjectRepository.delete(data);
	}
	
	public Page<Subject> getPaginatedMyEnrolledSubjects(Long student_id, int pageNumber) {

		Pageable pageable = PageRequest.of(pageNumber - 1, 4);
		
		List<StudentSubject> studentSubjectList = studentSubjectRepository.findByUser_id(student_id);

		List<Subject> myEnrolledSubjectsList = new ArrayList<>();

		for (StudentSubject student : studentSubjectList) {

			myEnrolledSubjectsList.add(student.getSubject());
		}
		
		final int start = (int)pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), myEnrolledSubjectsList.size());
		final Page<Subject> page = new PageImpl<>(myEnrolledSubjectsList.subList(start, end), pageable, myEnrolledSubjectsList.size());
		
		return page;
	}

	public List<Subject> getMySubjects(Long id) {

		return subjectRepository.findByUser_id(id);
	}

	public Page<Subject> getPaginatedMySubjects(Long id, int pageNumber) {

		Pageable pageable = PageRequest.of(pageNumber - 1, 4);
		return subjectRepository.findByUser_id(id, pageable);
	}

	public Page<Subject> getPaginatedSubjects(int pageNumber) {

		Pageable pageable = PageRequest.of(pageNumber - 1, 4);
		return subjectRepository.findAll(pageable);
	}

}
