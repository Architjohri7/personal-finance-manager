package com.syfe.pfm;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
  http.csrf(csrf->csrf.ignoringRequestMatchers("/api/**","/h2-console/**"))
   .headers(h->h.frameOptions(f->f.sameOrigin()))
   .authorizeHttpRequests(a->a.requestMatchers("/api/health","/api/auth/register","/api/auth/login","/error","/h2-console/**").permitAll().anyRequest().authenticated())
   .exceptionHandling(e->e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
   .formLogin(f->f.disable()).httpBasic(b->b.disable()).logout(l->l.disable())
   .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
  return http.build();
 }
}
