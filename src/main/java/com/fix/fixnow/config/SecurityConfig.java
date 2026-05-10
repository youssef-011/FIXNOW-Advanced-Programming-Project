package com.fix.fixnow.config;

import com.fix.fixnow.security.SessionAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final SessionAuthenticationFilter sessionAuthenticationFilter;

    public SecurityConfig(SessionAuthenticationFilter sessionAuthenticationFilter) {
        this.sessionAuthenticationFilter = sessionAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // CSRF is disabled for the local stable-demo flow because the app uses
                // simple server-rendered forms and API smoke tests without CSRF tokens.
                .csrf(AbstractHttpConfigurer::disable)
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/login", "/register", "/access-denied", "/error",
                                "/css/**", "/js/**", "/h2-console/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.POST, "/login", "/register", "/api/auth/register", "/api/auth/login").permitAll()

                        .requestMatchers("/api/auth/me", "/api/auth/logout").authenticated()

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/customer/**").hasRole("CUSTOMER")
                        .requestMatchers("/api/technicians/**").hasRole("TECHNICIAN")

                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/customer/**").hasRole("CUSTOMER")
                        .requestMatchers("/technician/**").hasRole("TECHNICIAN")

                        .anyRequest().authenticated()
                )

                .formLogin(AbstractHttpConfigurer::disable)

                .logout(l -> l.logoutUrl("/logout").logoutSuccessUrl("/login?logout"))

                .exceptionHandling(e -> e
                        .authenticationEntryPoint(this::handleUnauthenticated)
                        .accessDeniedHandler(this::handleAccessDenied)
                )

                .httpBasic(AbstractHttpConfigurer::disable)

                .addFilterBefore(sessionAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ================= CLEAN HANDLERS =================

    private void handleUnauthenticated(
            HttpServletRequest request,
            HttpServletResponse response,
            org.springframework.security.core.AuthenticationException ex
    ) throws java.io.IOException {
        if (isApiRequest(request)) {
            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }
        response.sendRedirect("/login");
    }

    private void handleAccessDenied(
            HttpServletRequest request,
            HttpServletResponse response,
            org.springframework.security.access.AccessDeniedException ex
    ) throws java.io.IOException {
        if (isApiRequest(request)) {
            writeJsonError(response, HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }
        response.sendRedirect("/access-denied");
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String path = request.getRequestURI();
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        return path.startsWith("/api/");
    }

    private void writeJsonError(HttpServletResponse response, int status, String message) throws java.io.IOException {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write("{\"status\":" + status + ",\"message\":\"" + message + "\"}");
    }
}
