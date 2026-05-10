package com.fix.fixnow.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class SessionAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        Authentication existing = SecurityContextHolder.getContext().getAuthentication();
        boolean missingRealUser = existing == null || existing instanceof AnonymousAuthenticationToken;

        if (session != null && missingRealUser) {

            Object email = session.getAttribute(SessionAuthConstants.AUTH_EMAIL);
            Object role = session.getAttribute(SessionAuthConstants.AUTH_ROLE);

            if (email instanceof String userEmail && role instanceof String userRole) {

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userEmail,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + userRole))
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}