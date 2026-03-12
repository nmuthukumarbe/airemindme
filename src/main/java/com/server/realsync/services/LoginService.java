package com.server.realsync.services;

import com.server.realsync.dto.LoginResponseDto;
import com.server.realsync.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public LoginResponseDto login(String username, String password) {
        User user = userService.findByUsername(username);
        LoginResponseDto loginResponseDto = new LoginResponseDto();

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            loginResponseDto.user_id = user.getId();
            loginResponseDto.account_id = user.getAccount().getId();
            loginResponseDto.email = user.getAccount().getEmail();

            // Set the authentication in the Security context
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            if (authentication.isAuthenticated()) {
                loginResponseDto.token =  jwtService.generateToken(username);
            } else {
                loginResponseDto.errorMessage =  "Invalid username or password";
            }
        } else {
            loginResponseDto.errorMessage =  "Invalid username or password";
        }

        return loginResponseDto;
    }


}
