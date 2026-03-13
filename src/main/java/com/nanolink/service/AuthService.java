package com.nanolink.service;

import com.nanolink.config.JwtUtil;
import com.nanolink.dto.AuthResponse;
import com.nanolink.dto.LoginRequest;
import com.nanolink.dto.RegisterRequest;
import com.nanolink.dto.RegisterResponse;
import com.nanolink.exception.InvalidCredentialsException;
import com.nanolink.exception.UserAlreadyExistsException;
import com.nanolink.models.User;
import com.nanolink.models.UserRole;
import com.nanolink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already exists: {}", request.getEmail());
            throw new UserAlreadyExistsException("Email already registered: " + request.getEmail());
        }
        
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username already exists: {}", request.getUsername());
            throw new UserAlreadyExistsException("Username already taken: " + request.getUsername());
        }

        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {} ({})", savedUser.getEmail(), savedUser.getUsername());

        return RegisterResponse.builder()
                .message("User registered successfully.")
                .email(savedUser.getEmail())
                .username(savedUser.getUsername())
                .build();
    }

    public AuthResponse login(LoginRequest request) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmailOrUsername(),
                            request.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String token = jwtUtil.generateToken(userDetails);

            log.info("User logged in successfully: {} ({})", userDetails.getUsername(), userDetails.getUsername());

            return AuthResponse.builder()
                    .token(token)
                    .build();

        } catch (BadCredentialsException e) {
            log.warn("Login failed - invalid credentials for: {}", request.getEmailOrUsername());
            throw new InvalidCredentialsException("Invalid email/username or password");
        }
    }
}