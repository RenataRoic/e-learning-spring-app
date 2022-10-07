package com.Application.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.Application.domain.Role;
import com.Application.domain.StudentSubject;
import com.Application.domain.User;
import com.Application.repository.StudentSubjectRepository;
import com.Application.repository.SubjectRepository;
import com.Application.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	SubjectRepository subjectRepository;

	@Autowired
	StudentSubjectRepository studentSubjectRepository;

	public HashMap doesUserExist(User user) {

		HashMap map = new HashMap();
		map.put("status", true);

		if (userRepository.findByEmail(user.getEmail()) != null) {
			map.put("status", false);
			map.put("emailExists", true);
		}

		if (userRepository.findByOib(user.getOib()) != null) {
			map.put("status", false);
			map.put("oibExists", true);
		}
		
		if ((int) (Math.log10(user.getOib()) + 1) != 11) {
			map.put("status", false);
			map.put("invalidOib", true);
		}


		return map;
	}
	
	public HashMap updateUserCheck(User user, User currentUserData) {

		HashMap map = new HashMap();
		map.put("status", true);

		if (userRepository.findByEmail(user.getEmail()) != null && currentUserData.getEmail().equals(user.getEmail()) == false ) {
			map.put("status", false);
			map.put("emailExists", true);
		}

		if (userRepository.findByOib(user.getOib()) != null && currentUserData.getOib() != user.getOib()) {
			map.put("status", false);
			map.put("oibExists", true);
		}
		
		if ((int) (Math.log10(user.getOib()) + 1) != 11) {
			map.put("status", false);
			map.put("invalidOib", true);
		}

		return map;
	}
	
	public HashMap changePasswordCheck(String email, String currentPassword, String newPassword, String confirmPassword) {

		HashMap map = new HashMap();
		map.put("status", true);

		User user = userRepository.findByEmail(email);
		
		if (user == null) {
			map.put("status", false);
			map.put("emailDoesntExist", true);
		}
		else {
			BCryptPasswordEncoder b = new BCryptPasswordEncoder();
			
			if (b.matches(currentPassword, user.getPassword()) == false) {
				map.put("status", false);
				map.put("wrongPassword", true);
			}
		}
		if (newPassword.equals(confirmPassword) == false ) {
			map.put("status", false);
			map.put("passwordsDontMatch", true);
		}

		return map;
	}

	public User saveProfessor(User user) {

		user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
		user.setRole(new Role(new Long(2)));
		return userRepository.save(user);
	}

	public User saveStudent(User user) {

		user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
		user.setRole(new Role(new Long(3)));
		return userRepository.save(user);
	}

	public List<User> getAllUsers() {

		return userRepository.findAll();
	}

	public List<User> getAllUsersByRoleId(Long id) {

		return userRepository.findByRole_id(id);
	}

	public User getUser(Long id) {

		return userRepository.findById(id).get();
	}

	public void updateUser(User editedUser, Long user_id) {

		User user = userRepository.findById(user_id).get();
		user.setName(editedUser.getName());
		user.setSurname(editedUser.getSurname());
		user.setOib(editedUser.getOib());
		user.setEmail(editedUser.getEmail());
		
		userRepository.save(user);
	}

	public void deleteUserById(Long id) {

		userRepository.deleteById(id);
	}
	
	public List<StudentSubject> getEnrolledStudents(Long id){
		
		return studentSubjectRepository.findBySubject_id(id);
	}
	
	public Page<User> getPaginatedEnrolledStudents(Long subject_id, int pageNumber) {

		Pageable pageable = PageRequest.of(pageNumber - 1, 4);
		
		List<StudentSubject> studentSubjectList = studentSubjectRepository.findBySubject_id(subject_id);
		
		List<User> enrolledStudentsList = new ArrayList<>();
		
		for (StudentSubject subject : studentSubjectList) {

			enrolledStudentsList.add(subject.getUser());
		}
		
		final int start = (int)pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), enrolledStudentsList.size());
		final Page<User> page = new PageImpl<>(enrolledStudentsList.subList(start, end), pageable, enrolledStudentsList.size());
		
		return page;
	}

	public void changePassword(String email, String currentPassword, String newPassword, String confirmPassword) {

		User user = userRepository.findByEmail(email);
		BCryptPasswordEncoder b = new BCryptPasswordEncoder();
		user.setPassword(b.encode(newPassword));
		
		userRepository.save(user);
	
	}

	public Page<User> getPaginatedProfessors(int pageNumber) {

		Pageable pageable = PageRequest.of(pageNumber - 1, 4);
		return userRepository.findByRole_id(new Long(2), pageable);
	}

	public Page<User> getPaginatedStudents(int pageNumber) {

		Pageable pageable = PageRequest.of(pageNumber - 1, 4);
		return userRepository.findByRole_id(new Long(3), pageable);
	}

}
