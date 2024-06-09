package com.joe.spring_security_jwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joe.spring_security_jwt.models.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
  
  Optional<Role> findByName(String name);

}
