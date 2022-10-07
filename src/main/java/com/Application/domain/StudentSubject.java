package com.Application.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentSubject {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	public StudentSubject(User user, Subject subject) {
		this.user = user;
		this.subject = subject;
	}

	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	@ManyToOne
	@JoinColumn(name="subject_id")
	private Subject subject;
	
}
