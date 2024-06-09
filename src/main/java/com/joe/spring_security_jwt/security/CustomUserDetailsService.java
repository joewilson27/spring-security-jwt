package com.joe.spring_security_jwt.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.joe.spring_security_jwt.models.Role;
import com.joe.spring_security_jwt.models.UserEntity;
import com.joe.spring_security_jwt.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * We are about to create our custom UserDetailsService
 * 
 * By defining the CustomUserDetailsService as a @Service, Spring will AUTOMATICALLY detect and use it when configuring 
 * the AuthenticationManager. The PasswordEncoder bean is also automatically used by Spring Security.
 */

@Service // make sure make this class to be a service otherwise it won't work
public class CustomUserDetailsService implements UserDetailsService {
//public class CustomUserDetailsService {

  @Autowired
  private UserRepository userRepository;

  // public CustomUserDetailsService(UserRepository userRepository) {
  //   this.userRepository = userRepository;
  // }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    
    UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));

    return new User(user.getUsername(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));
  }

  private Collection<GrantedAuthority> mapRolesToAuthorities(List<Role> roles) {
    return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
  }
  
}
