package com.example.selfstudybe.configs;

import com.example.selfstudybe.security.CustomAuthenticationManager;
import com.example.selfstudybe.security.CustomFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@AllArgsConstructor
public class SecurityConfig {
    private final CustomAuthenticationManager authenticationManager;
    private final CustomFilter customFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .requireCsrfProtectionMatcher(request -> false)
                )
                .oauth2Login(Customizer.withDefaults())
                .authenticationManager(authenticationManager)
                .addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->{
                    auth.requestMatchers("/swagger-ui/**","/v3/api-docs*/**","/v1/auth/**").permitAll();
                    auth.anyRequest().authenticated();});
        return http.build();
    }
}
