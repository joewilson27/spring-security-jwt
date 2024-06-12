package com.joe.spring_security_jwt.controllers;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/user")
public class UserController {
  
  /**
   * Explanation
    - @AuthenticationPrincipal: This annotation can be used on a method parameter in a controller to inject the UserDetails of the currently authenticated user.
    - UserDetails: The method parameter UserDetails userDetails will be populated with the details of the authenticated user.
    - getUserDetails Method: This method is mapped to the /user URL and returns the username and authorities of the authenticated  user.
    By using this approach, you can easily access the authenticated user's details in your controller methods and perform any necessary logic based on that information.
   */
  @GetMapping("/details")
  public String getDetails(@AuthenticationPrincipal UserDetails userDetails) {
    // sample return value
    return userDetails.getUsername();
  }

  @GetMapping("/other-details")
  public String getOtherDetails(Authentication authentication) {
    return "/other-details " + authentication.getName();
  }

}
