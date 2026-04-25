package com.fix.fixnow.controller;

import com.fix.fixnow.dto.LoginDTO;
import com.fix.fixnow.dto.RegisterDTO;
import com.fix.fixnow.dto.UserDTO;
import com.fix.fixnow.exception.BadRequestException;
import com.fix.fixnow.model.User;
import com.fix.fixnow.security.SessionAuthConstants;
import com.fix.fixnow.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO register(@Valid @RequestBody RegisterDTO registerDTO, HttpSession session) {
        User user = new User();
        user.setName(registerDTO.getName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());
        user.setRole(registerDTO.getRole());
        User savedUser = authService.register(user);
        storeUserInSession(session, savedUser);
        return toUserDTO(savedUser);
    }

    @PostMapping("/login")
    public UserDTO login(@Valid @RequestBody LoginDTO loginDTO, HttpSession session) {
        return authService.login(loginDTO.getEmail(), loginDTO.getPassword())
                .map(user -> {
                    storeUserInSession(session, user);
                    return toUserDTO(user);
                })
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));
    }

    @GetMapping("/me")
    public UserDTO me(HttpSession session) {
        Object id = session.getAttribute(SessionAuthConstants.AUTH_USER_ID);
        Object name = session.getAttribute(SessionAuthConstants.AUTH_NAME);
        Object email = session.getAttribute(SessionAuthConstants.AUTH_EMAIL);
        Object role = session.getAttribute(SessionAuthConstants.AUTH_ROLE);

        if (!(id instanceof Long userId) || !(name instanceof String userName)
                || !(email instanceof String userEmail) || !(role instanceof String userRole)) {
            throw new BadRequestException("No active session");
        }

        UserDTO dto = new UserDTO();
        dto.setId(userId);
        dto.setName(userName);
        dto.setEmail(userEmail);
        dto.setRole(Enum.valueOf(com.fix.fixnow.model.Role.class, userRole));
        return dto;
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpSession session) {
        session.invalidate();
    }

    private UserDTO toUserDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    private void storeUserInSession(HttpSession session, User user) {
        session.setAttribute(SessionAuthConstants.AUTH_USER_ID, user.getId());
        session.setAttribute(SessionAuthConstants.AUTH_NAME, user.getName());
        session.setAttribute(SessionAuthConstants.AUTH_EMAIL, user.getEmail());
        session.setAttribute(SessionAuthConstants.AUTH_ROLE, user.getRole().name());
    }
}
