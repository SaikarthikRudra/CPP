package com.exavalu.customer.product.portal.jwt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
class JWTUserConfig {
	
	@Value("${jwt.admin.userName}")
    private String adminUserName;
	@Value("${jwt.admin.userName}")
    private String adminPassword;
	
	@Value("${jwt.user.userName}")
    private String userUserName;
	@Value("${jwt.user.password}")
    private String userPassword;
	
    @Bean
    public UserDetailsService userDetailsService() {
    	 UserDetails admin = User.builder()
                 .username(this.adminUserName)
                 .password(passwordEncoder().encode(this.adminPassword))
                 .roles("ADMIN")
                 .build();
         
         UserDetails user = User.builder()
                 .username(this.userUserName)
                 .password(passwordEncoder().encode(this.userPassword))
                 .roles("USER")
                 .build();

         return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
        return builder.getAuthenticationManager();
    }
}