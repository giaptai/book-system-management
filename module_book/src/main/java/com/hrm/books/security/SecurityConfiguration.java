package com.hrm.books.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity(debug = false)
public class SecurityConfiguration {
    private final VisitorDetailService visitorDetailService;
    private final JWTFilter jwtAuthFilter;

    public SecurityConfiguration(VisitorDetailService visitorDetailService, JWTFilter jwtAuthFilter) {
        this.visitorDetailService = visitorDetailService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry ->
                                //https://stackoverflow.com/questions/70906081/springboot-swagger3-failed-to-load-remote-configuration
                                registry
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/category/**", "/book/**", "/author/**", "/statistics/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/login", "/register", "/buy").permitAll()
                                .requestMatchers("/my-account/**").hasRole("USER")
                                .anyRequest().hasAnyRole("WORKER", "ADMIN", "GOD")
                )
                .exceptionHandling(exceptionHandler ->
                        exceptionHandler
                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                    response.sendError(403, "CẤM TRUY CẬP OK ");
                                }))
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider()).addFilterBefore(
                        jwtAuthFilter, UsernamePasswordAuthenticationFilter.class
                );
        return httpSecurity.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return visitorDetailService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(visitorDetailService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        String idForEncode = "noop";
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(idForEncode, NoOpPasswordEncoder.getInstance());
        return new DelegatingPasswordEncoder(idForEncode, encoders);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }
}
