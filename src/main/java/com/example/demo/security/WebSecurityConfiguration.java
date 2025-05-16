package com.example.demo.security;


import org.springframework.context.annotation.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.http.*;
import org.springframework.security.web.*;


@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    private final AuthenticationConfiguration authenticationConfiguration;

    public WebSecurityConfiguration(AuthenticationConfiguration authenticationConfiguration) {
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL)
                            .permitAll();
                    registry.anyRequest()
                            .authenticated();

                })
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .addFilter(new JWTAuthenticationVerificationFilter(authenticationManager()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .build();
    }


    @Bean
    public AuthenticationManager authenticationManager()
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}