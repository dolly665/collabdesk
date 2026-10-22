package com.in6206.collabdesk.config;

import com.in6206.collabdesk.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // antMatcher(...) everywhere: with the H2 console servlet registered, plain string
            // matchers are ambiguous in Spring Security 6 and fail at startup
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(antMatcher("/login"), antMatcher("/register"), antMatcher("/error"),
                        antMatcher("/css/**"), antMatcher("/js/**"), antMatcher("/images/**")).permitAll()
                .requestMatchers(antMatcher("/h2-console/**")).permitAll()
                .requestMatchers(antMatcher("/creator/**")).hasRole("CREATOR")
                .requestMatchers(antMatcher("/brand/**")).hasRole("BRAND")
                .requestMatchers(antMatcher("/admin/**"), antMatcher("/api/admin/**")).hasRole("ADMIN")
                .anyRequest().authenticated())
            // session-based form login for the web UI
            .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/", true).permitAll())
            // HTTP Basic so the REST API can also be called from Postman
            .httpBasic(Customizer.withDefaults())
            // own login page (LoginController) handles ?error and ?logout itself
            .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
            // REST endpoints are called by Swagger/Postman, H2 console uses its own forms
            .csrf(csrf -> csrf.ignoringRequestMatchers(antMatcher("/api/**"), antMatcher("/h2-console/**")))
            .headers(h -> h.frameOptions(f -> f.sameOrigin()));
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository users) {
        return username -> users.findByUsername(username)
                .map(u -> org.springframework.security.core.userdetails.User
                        .withUsername(u.getUsername())
                        .password(u.getPasswordHash())
                        .roles(u.getRole().name())
                        .disabled(!u.isEnabled())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
