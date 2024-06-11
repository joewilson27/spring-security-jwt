package com.joe.spring_security_jwt.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Let’s define a filter that executes once per request. So we create AuthTokenFilter class that extends OncePerRequestFilter and override doFilterInternal() method.
 * 
 * OncePerRequestFilter makes a single execution for each request to our API. It provides a doFilterInternal() method that we will implement parsing & validating JWT, 
 * loading User details (using CustomUserDetailsService), checking Authorization (using UsernamePasswordAuthenticationToken).
 */

public class JWTAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  private JWTGenerator tokenGenerator;

  @Autowired
  private CustomUserDetailsService customUserDetailsService;

  /**
   * So doFilterInternal is a link in the Security Filter Chain where BEFORE we actually get to the controllers
   * it's going to PERFORM a CHECK to SEE if there's actually a token within the header (on request) 
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
    System.out.println("in doFilterInternal JWTAuthenticationFilter");
    // Get token from the request (header)
    String token = getJWTFromRequest(request);
    if (StringUtils.hasText(token) && tokenGenerator.validateToken(token)) {
      // token exists, then get username from token
      String username = tokenGenerator.getUsernameFromJWT(token);

      // check username from the table user
      UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

      UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
    filterChain.doFilter(request, response);
  }

  private String getJWTFromRequest(HttpServletRequest request) {
    // get token out from the headers
    String bearerToken = request.getHeader("Authorization");
    String tokenType = "Bearer ";

    if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(tokenType)) {
      return bearerToken.substring(tokenType.length(), bearerToken.length()); // public String substring(int start, int end)
    }
    return null;
  }
  
}
