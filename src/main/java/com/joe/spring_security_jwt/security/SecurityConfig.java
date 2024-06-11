package com.joe.spring_security_jwt.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration // this annotation for let the Spring Boot knows that this is a configuration file, and this needs to be added to the bean context
@EnableWebSecurity // it will let Spring Boot know that this is where we're keeping our security configuration
public class SecurityConfig {
  
  private JwtAuthEntryPoint authEntryPoint;
  
  // private CustomUserDetailsService userDetailsService;

  // private final CustomUserDetailsService userDetailsService;

  public SecurityConfig(
    //CustomUserDetailsService customUserDetailsService
    JwtAuthEntryPoint authEntryPoint) {
    //this.userDetailsService = customUserDetailsService;
    this.authEntryPoint = authEntryPoint;
  }



  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    /**
     * This is where we're going to have our security filter chain and this is where we are going to
     * configure our security filter chain. 
     * So, is this a true security filter chain? Not really, this is more or less just how we're going to
     * configure our security filter chain because we need that in order for the actual routing and for to actually
     * have a place for a request (http request from client) to go before they get to the actual controller.
     * Because if we don't have this filter chain there will be no way for the request to be intercept did before 
     * they actually get to the controllers
     */
    // version from the tutorial
    // http
    //     .csrf().disable() // this isn't a production, so disable csrf, because if we don't, we may have a bunch of errors
    //     // start handling JWT authentication
    //     .exceptionHandling()
    //     .authenticationEntryPoint(authEntryPoint)
    //     .and()
    //     .sessionManagement()
    //     .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    //     // end handling JWT authentication
    //     .and()
    //     .authorizeRequests()
    //     .antMatchers("/api/auth/**").permitAll()
    //     .anyRequest().authenticated()
    //     .and()
    //     .httpBasic();
    //http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    System.out.println("in filterChain SecurityConfig");
    http
        .csrf(csrf -> csrf.disable())
        // start handling JWT authentication
        // source adding handles to the newest version = https://stackoverflow.com/a/76762264/9838277
        .exceptionHandling(
          eh -> eh.authenticationEntryPoint(authEntryPoint)
        )
        .sessionManagement(
          sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        // end handling JWT authentication
        .authorizeHttpRequests( auth ->
          auth.requestMatchers("/api/auth/**", "/public/**").permitAll()
              .anyRequest().authenticated()
        )
        .httpBasic(withDefaults());

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // Customize form login if needed
        // http.formLogin(formLogin -> 
        //     formLogin
        //         .loginPage("/login")
        //         .permitAll()
        // );

    // http
    //     .csrf(csrf -> csrf.disable())
    //     .authorizeHttpRequests( auth ->
    //       auth.requestMatchers("/api/auth/**").permitAll()
    //           .requestMatchers("/api/auth/admin-site").hasRole("ADMIN")
    //           .requestMatchers("/api/auth/user-site").hasRole("USER")
    //           .anyRequest().authenticated()
    //     )
    //     .formLogin(withDefaults())
    //     .httpBasic(withDefaults());

    return http.build();
  }

  /**
   * If this UserDetailsService still there, so what's happening is that
   * if that UserDetailsService it overrides our CustomUserDetailsService class and makes it so that
   * we can't login
   * 
   * jika kita ingin menggunakan manual user, maka kita tidak perlu membuat CustomUserDetailsService, dan hanya cukup membuat method
   * user() ini menjadi Bean UserDetailsService (uncomment Bean)
   * @return
   */
  // @Bean
  public UserDetailsService user() { // this Bean will return a UserDetailsService
    PasswordEncoder encoder = passwordEncoder();
    UserDetails admin = User.builder()
                        .username("admin")
                        .password(encoder.encode("password"))
                        .roles("ADMIN")
                        .build();

    UserDetails user = User.builder()
                        .username("user")
                        .password(encoder.encode("password")) // .password("{noop}password") with no encoder
                        .roles("USER")
                        .build();

    return new InMemoryUserDetailsManager(admin, user);
    /**
     * this userdetails that we build here is responsible to filtering later for our users
     */
  }

  // Use this approach and bean context will automatically detect CustomUserDetails as service that implements UserDetailsService and use it
  // when configuring the AuthenticationManager
  /**
   * So this is IMPORTANT, look at the login method in AuthController, they're using AuthenticationManager to check the user logins
   * and the user login will matched from the userdetails that we are checked in CustomUserDetailsService in method loadUserByUsername()
   * 
   */ 
  @Bean
  public AuthenticationManager authenticationManager(
    AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  // other approach to create AuthenticationManager Bean
  // @Bean
  // public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
  //     AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
  //     auth
  //         .userDetailsService(userDetailsService)
  //         .passwordEncoder(passwordEncoder());
  //     return auth.build();
  // }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public JWTAuthenticationFilter jwtAuthenticationFilter() {
    return new JWTAuthenticationFilter();
  } 

}
