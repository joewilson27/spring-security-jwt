package com.joe.spring_security_jwt.dto;

import lombok.Data;

@Data
public class LoginDto {
  
  private String username;

  private String password;

}
