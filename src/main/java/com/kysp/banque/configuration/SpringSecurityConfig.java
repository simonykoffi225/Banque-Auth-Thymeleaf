package com.kysp.banque.configuration;

import com.kysp.banque.repository.CompteRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private final CompteRepository compteRepository;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public SpringSecurityConfig(CompteRepository compteRepository, CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.compteRepository = compteRepository;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> {
            auth.requestMatchers("/banque/modifier", "/banque/supprimer/**", "/defaultPageAdmin")
                .hasRole("ADMIN");

            auth.requestMatchers("/banque/ouvrir", "/banque/comptes", "/banque/compte/**",
                    "/banque/depot/**", "/banque/retrait/**", "/banque/transfert", "/defaultPage")
                .hasAnyRole("USER", "ADMIN");

            auth.requestMatchers("/signup", "/login")  // Permettre l'accès public à signup et login
                .permitAll();

            auth.anyRequest().authenticated();
        })
        .formLogin(form -> form
            .loginPage("/login")
            .successHandler(customAuthenticationSuccessHandler)
            .failureUrl("/login?error=true")
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout=true")
            .permitAll()
        );

    return http.build();
}



    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            com.kysp.banque.models.Compte compte = compteRepository.findByUsername(username);
            if (compte == null) {
                throw new UsernameNotFoundException("User not found");
            }
            return org.springframework.security.core.userdetails.User
                .withUsername(compte.getUsername())
                .password(compte.getPassword())
                .roles(compte.getRole().replace("ROLE_", ""))
                .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
