package com.Application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import com.Application.domain.Subject;
import com.Application.domain.User;
import com.Application.service.SubjectService;
import com.Application.service.UserService;
import com.Application.domain.CustomUserDetails;
import com.Application.domain.StudentSubject;

@Controller
public class homeController {

	@Autowired
	UserService userService;

	@Autowired
	SubjectService subjectService;

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/addProfessor")
	public String addProfessor() {
		return "addProfessor";
	}

	@PostMapping("/addProfessor")
	public String addProfessorPost(User user, BindingResult result, Model model) {

		HashMap map = userService.doesUserExist(user);

		if (map.get("status").equals(true)) {
			userService.saveProfessor(user);
			return "redirect:/allProfessors/page/1";
		} else {
			model.addAttribute("error", map);
			return "addProfessor";
		}
	}

	@GetMapping("/addStudent")
	public String addStudent() {
		return "addStudent";
	}

	@PostMapping("/addStudent")
	public String addStudentPost(User user, Model model, BindingResult result) {
		if (result.hasErrors()) {
			return "addStudent";
		}
		HashMap map = userService.doesUserExist(user);

		if (map.get("status").equals(true)) {
			userService.saveStudent(user);
			return "redirect:/allStudents/page/1";
		} else {
			model.addAttribute("error", map);
			return "addStudent";
		}
	}

	@GetMapping("/addSubject")
	public String addSubject(Model model) {

		model.addAttribute("professorList", userService.getAllUsersByRoleId(new Long(2)));
		return "addSubject";
	}

	@PostMapping("/addSubject")
	public String addSubjectPost(@ModelAttribute("subjectFormData") Subject formData) {
		
		subjectService.saveSubject(formData);
		return "redirect:allSubjects/page/1";
	}

	@GetMapping("/allProfessors/page/{num}")
	public String allProfessors(Model model, @PathVariable(value = "num") int currentPage) {

		Page<User> user = userService.getPaginatedProfessors(currentPage);

		int totalPages = user.getTotalPages();
		long totalItems = user.getTotalElements();

		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalItems", totalItems);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("professorList", user.getContent());

		return "allProfessors";
	}

	@GetMapping("/allStudents/page/{num}")
	public String allStudents(Model model, @PathVariable(value = "num") int currentPage) {

		Page<User> user = userService.getPaginatedStudents(currentPage);

		int totalPages = user.getTotalPages();
		long totalItems = user.getTotalElements();

		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalItems", totalItems);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("studentList", user.getContent());

		return "allStudents";
	}

	@GetMapping("/allSubjects/page/{num}")
	public String allSubjects(Model model, @PathVariable(value = "num") int currentPage) {

		Page<Subject> subject = subjectService.getPaginatedSubjects(currentPage);

		int totalPages = subject.getTotalPages();
		long totalItems = subject.getTotalElements();

		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalItems", totalItems);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("subjectList", subject.getContent());

		return "allSubjects";
	}

	@GetMapping("/allProfessors/{id}")
	String professorInfo(Model model, @PathVariable(value = "id") Long id) {

		model.addAttribute("user", userService.getUser(id));
		return "professorInfo";
	}

	@GetMapping("/allStudents/{id}")
	String studentInfo(Model model, @PathVariable(value = "id") Long id) {

		model.addAttribute("user", userService.getUser(id));
		return "studentInfo";

	}

	@GetMapping("/allSubjects/{id}")
	String subjectInfo(Model model, @PathVariable(value = "id") Long id,
			@AuthenticationPrincipal CustomUserDetails user_details) {

		model.addAttribute("subject", subjectService.getSubject(id));
		model.addAttribute("enroll", subjectService.checkEnrollment(id, user_details.getUser()));
		return "subjectInfo";

	}

	@GetMapping("/updateUser/{id}")
	public String updateUser(Model model, @PathVariable(value = "id") Long id, User user) {

		model.addAttribute("user", userService.getUser(id));

		return "updateUser";
	}

	@PostMapping("/updateUser/{id}")
	public String updateUserPost(Model model, @ModelAttribute("user") User user, @PathVariable(value = "id") Long id) {

		User currentUserData = userService.getUser(id);

		HashMap map = userService.updateUserCheck(user, currentUserData);
		if (map.get("status").equals(true)) {

			userService.updateUser(user, id);

			if (currentUserData.getRole().getId() == 2) {
				return "redirect:/allProfessors/" + id;
			} else {
				return "redirect:/allStudents/" + id;
			}
		} else {
			System.out.println(map);
			model.addAttribute("error", map);
			return "updateUser";
		}

	}

	@GetMapping("/updateSubject/{id}")
	public String updateSubject(Model model, @PathVariable(value = "id") Long id, Subject subject) {

		model.addAttribute("subject", subjectService.getSubject(id));
		model.addAttribute("professorList", userService.getAllUsersByRoleId(new Long(2)));

		return "updateSubject";
	}

	@PostMapping("/updateSubject/{id}")
	public String updateSubjectPost(@ModelAttribute("subject") Subject subject) {
		
		subjectService.saveSubject(subject);
		return "redirect:/allSubjects/{id}";
	}

	@GetMapping("/deleteUser")
	public String deleteUser(@ModelAttribute("id") Long id) {

		User user = userService.getUser(id);

		if (user.getRole().getId() == 2) {
			userService.deleteUserById(id);
			return "redirect:/allProfessors/page/1";
		} else {
			userService.deleteUserById(id);
			return "redirect:/allStudents/page/1";
		}
	}

	@GetMapping("/deleteSubject")
	public String deleteSubject(@ModelAttribute("id") Long id) {

		subjectService.deleteSubjectbyId(id);
		return "redirect:/allSubjects/page/1";
	}

	@GetMapping("/enroll")
	public String enrollInSubject(Long id, @AuthenticationPrincipal CustomUserDetails user_details) {

		subjectService.enroll(user_details.getUser(), id);
		return "redirect:/allSubjects/" + id;
	}

	@GetMapping("/unenrollStudent")
	public String unenrollStudent(Long subject_id, Long student_id) {

		subjectService.unenrollStudent(student_id, subject_id);
		return "redirect:/allSubjects/" + subject_id + "/enrolledStudents/page/1";
	}

	@GetMapping("/manageEnrollment/page/{num}")
	public String manageEnrollment(Model model, Long id, @PathVariable(value = "num") int currentPage) {

		Page<User> user = userService.getPaginatedStudents(currentPage);

		int totalPages = user.getTotalPages();
		long totalItems = user.getTotalElements();

		model.addAttribute("subject", subjectService.getSubject(id));

		List<StudentSubject> studentSubjectList = userService.getEnrolledStudents(id);

		List<Long> studentIdList = new ArrayList<>();

		for (StudentSubject subject : studentSubjectList) {

			studentIdList.add(subject.getUser().getId());
		}

		model.addAttribute("studentIdList", studentIdList);

		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalItems", totalItems);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("studentList", user.getContent());

		return "manageEnrollment";
	}
	
	@GetMapping("/changeStatus")
	public String changeStatus(Long subject_id, Long student_id, int num) {

		User user = userService.getUser(student_id);
		subjectService.changeStatus(user, subject_id);

		return "redirect:/manageEnrollment/page/" + num + "?id=" + subject_id;
	}

	@GetMapping("/allSubjects/{id}/enrolledStudents/page/{num}")
	public String enrolledStudents(Model model, @PathVariable(value = "id") Long id, @PathVariable(value = "num") int currentPage) {

		model.addAttribute("subject", subjectService.getSubject(id));
		
		Page<User> enrolledStudentsList = userService.getPaginatedEnrolledStudents(id, currentPage);

		int totalPages = enrolledStudentsList.getTotalPages();
		long totalItems = enrolledStudentsList.getTotalElements();

		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalItems", totalItems);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("enrolledStudentsList", enrolledStudentsList.getContent());

		return "enrolledStudents";
	}

	@GetMapping("myProfile/{id}")
	public String myProfile(Model model, @PathVariable(value = "id") Long id) {

		model.addAttribute("user", userService.getUser(id));
		return "myProfile";
	}

	@GetMapping("/myProfile/{id}/myEnrolledSubjects/page/{num}")
	public String myEnrolledSubjects(Model model, @PathVariable(value = "id") Long id, @PathVariable(value = "num") int currentPage) {

		Page<Subject> myEnrolledSubjectsList = subjectService.getPaginatedMyEnrolledSubjects(id, currentPage);

		int totalPages = myEnrolledSubjectsList.getTotalPages();
		long totalItems = myEnrolledSubjectsList.getTotalElements();

		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalItems", totalItems);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("userId", id);
		model.addAttribute("myEnrolledSubjectsList", myEnrolledSubjectsList.getContent());

		return "myEnrolledSubjects";
	}

	@GetMapping("/allStudents/{id}/studentsSubjects/page/{num}")
	public String studentsSubjects(Model model, @PathVariable(value = "id") Long id, @PathVariable(value = "num") int currentPage) {

		Page<Subject> studentsSubjectsList = subjectService.getPaginatedMyEnrolledSubjects(id, currentPage);

		User user = userService.getUser(id);

		int totalPages = studentsSubjectsList.getTotalPages();
		long totalItems = studentsSubjectsList.getTotalElements();

		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalItems", totalItems);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("user", user);
		model.addAttribute("studentsSubjectsList", studentsSubjectsList.getContent());

		return "studentsSubjects";
	}

	@GetMapping("/myProfile/{id}/mySubjects/page/{num}")
	public String mySubjects(Model model, @PathVariable(value = "id") Long id, @PathVariable(value = "num") int currentPage) {

		Page<Subject> subject = subjectService.getPaginatedMySubjects(id, currentPage);

		int totalPages = subject.getTotalPages();
		long totalItems = subject.getTotalElements();

		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalItems", totalItems);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("mySubjectsList", subject.getContent());

		return "mySubjects";
	}

	@GetMapping("/allProfessors/{id}/professorsSubjects/page/{num}")
	public String professorsSubjects(Model model, @PathVariable(value = "id") Long id, @PathVariable(value = "num") int currentPage) {

		Page<Subject> subject = subjectService.getPaginatedMySubjects(id, currentPage);

		User user = userService.getUser(id);

		int totalPages = subject.getTotalPages();
		long totalItems = subject.getTotalElements();

		model.addAttribute("user", user);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("totalItems", totalItems);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("professorsSubjectsList", subject.getContent());

		return "professorsSubjects";
	}

	@GetMapping("/changePassword")
	public String changePassword(Model model) {

		return "changePassword";
	}

	@PostMapping("/changePassword")
	public String changePasswordPost(Model model, @RequestParam("email") String email,
												  @RequestParam("currentPassword") String currentPassword, 
												  @RequestParam("newPassword") String newPassword,
												  @RequestParam("confirmPassword") String confirmPassword) {

		HashMap map = userService.changePasswordCheck(email, currentPassword, newPassword, confirmPassword);

		if (map.get("status").equals(true)) {
			userService.changePassword(email, currentPassword, newPassword, confirmPassword);
			return "redirect:/login";
		} else {
			model.addAttribute("error", map);
			return "changePassword";
		}
	}

}
