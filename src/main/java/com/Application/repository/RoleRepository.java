package com.Application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Application.domain.Role;

@Repository

public interface RoleRepository extends JpaRepository<Role, Long> {

}
