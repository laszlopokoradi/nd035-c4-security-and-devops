package com.example.demo.security;


import com.auth0.jwt.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.*;
import org.springframework.security.web.authentication.www.*;
import org.springframework.stereotype.*;

import java.io.*;
import java.util.*;

import static com.auth0.jwt.algorithms.Algorithm.*;


@Component
public class JWTAuthenticationVerificationFilter
        extends BasicAuthenticationFilter {

    public JWTAuthenticationVerificationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String header = req.getHeader(SecurityConstants.HEADER_STRING);

        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext()
                             .setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req) {
        String token = req.getHeader(SecurityConstants.HEADER_STRING);
        if (token != null) {
            String user = JWT.require(HMAC512(SecurityConstants.SECRET.getBytes()))
                             .build()
                             .verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
                             .getSubject();
            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }
            return null;
        }
        return null;
    }
}