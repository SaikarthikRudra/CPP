package com.exavalu.customer.product.portal.jwt.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.exavalu.customer.product.portal.jwt.authentication.JwtAuthenticationEntryPoint;
import com.exavalu.customer.product.portal.jwt.authentication.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint point;

    @Autowired
    private JwtAuthenticationFilter filter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    	 http.csrf(csrf -> csrf.disable())
         .authorizeHttpRequests(authz -> authz
                 .requestMatchers("/v1/**").hasAnyRole("USER","ADMIN")
                 .requestMatchers("/admin/**").hasRole("ADMIN")
                 .requestMatchers("/auth/login").permitAll()
                 .anyRequest().authenticated()
         )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(point))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

       
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
