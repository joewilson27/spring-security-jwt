package com.joe.spring_security_jwt.controllers;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joe.spring_security_jwt.dto.AuthResponseDTO;
import com.joe.spring_security_jwt.dto.LoginDto;
import com.joe.spring_security_jwt.dto.RegisterDto;
import com.joe.spring_security_jwt.models.Role;
import com.joe.spring_security_jwt.models.UserEntity;
import com.joe.spring_security_jwt.repository.RoleRepository;
import com.joe.spring_security_jwt.repository.UserRepository;
import com.joe.spring_security_jwt.security.JWTGenerator;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  
  @Autowired(required = false)
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JWTGenerator jwtGenerator;

  // public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
  //     RoleRepository roleRepository, PasswordEncoder passwordEncoder, JWTGenerator jwtGenerator) {
  //   this.authenticationManager = authenticationManager;
  //   this.userRepository = userRepository;
  //   this.roleRepository = roleRepository;
  //   this.passwordEncoder = passwordEncoder;
  //   this.jwtGenerator = jwtGenerator
  // }

  @PostMapping("login")
  public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDto request) {
    System.out.println("in login controller");
    // we need to use AuthenticationManager to produce authentication object 
    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        request.getUsername(),
        request.getPassword())
    );
    /**
     * This security context is going to hold all of the authentication details (above) so that whenever the user logs in
     * they DON'T have to keep logging in and all this stored within the context and Spring Security handles this all for
     */
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String token = jwtGenerator.generateToken(authentication);

    return new ResponseEntity<>(new AuthResponseDTO(token), HttpStatus.OK);
  }


  @PostMapping("register")
  public ResponseEntity<String> register(@RequestBody RegisterDto request) {

    if (userRepository.existsByUsername(request.getUsername())) {
      return new ResponseEntity<>("Username is taken!", HttpStatus.BAD_REQUEST);
    }

    UserEntity user = new UserEntity();
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword()));

    Role roles = roleRepository.findByName("USER").get();
    user.setRoles(Collections.singletonList(roles));

    userRepository.save(user);

    return new ResponseEntity<>("User registered success!", HttpStatus.OK);
  }

  @PostMapping("login-new")
  public ResponseEntity<String> loginNew(@RequestBody LoginDto request) {

    UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new ResponseEntity<>("Authenticated user: " + request.getUsername(), HttpStatus.OK);
    
  }

  @GetMapping("admin-site")
  public ResponseEntity<String> adminSite() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    return new ResponseEntity<>(String.format("Authen for admin %s ", authentication.getName()), HttpStatus.OK);
  }
  @GetMapping("user-site")
  public ResponseEntity<String> userSite() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    return new ResponseEntity<>(String.format("Authen for user %s ", authentication.getName()), HttpStatus.OK);
  }

}
