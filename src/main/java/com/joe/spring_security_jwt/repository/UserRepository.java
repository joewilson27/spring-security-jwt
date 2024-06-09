package com.joe.spring_security_jwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.joe.spring_security_jwt.models.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
  
  Optional<UserEntity> findByUsername(String username);

  Boolean existsByUsername(String username); 

}
