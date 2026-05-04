package com.example.CharityProject.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        // On ignore le CSRF pour les APIs (Postman)
                        // ET pour les routes de gestion d'actions si nécessaire pour tes tests
                        .ignoringRequestMatchers("/api/**", "/actions/**")
                )
                .authorizeHttpRequests(auth -> auth
                        // 1. Ressources Statiques et Auth
                        .requestMatchers("/", "/actions/detail/**", "/login", "/register", "/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()

                        // 2. Accès Public API
                        .requestMatchers("/api/**").permitAll()

                        // 3. Accès Restreint : Seules les Organisations peuvent créer/gérer
                        .requestMatchers("/actions/creer", "/actions/save", "/actions/dashboard", "/actions/editer/**").hasRole("ORGANISATION")
                        .requestMatchers("/actions/donner").hasRole("USER")

                        // 4. Accès Admin
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 5. Par défaut : Authentification requise
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        // REDIRECTION INTELLIGENTE :
                        .successHandler((request, response, authentication) -> {
                            var roles = authentication.getAuthorities();
                            boolean isOrga = roles.stream()
                                    .anyMatch(r -> r.getAuthority().equals("ROLE_ORGANISATION"));

                            if (isOrga) {
                                response.sendRedirect("/actions/dashboard");
                            } else {
                                response.sendRedirect("/profile");
                            }
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}