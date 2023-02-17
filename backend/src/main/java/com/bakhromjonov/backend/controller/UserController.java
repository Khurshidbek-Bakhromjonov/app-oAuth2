package com.bakhromjonov.backend.controller;

import com.bakhromjonov.backend.exception.ResourceNotFoundException;
import com.bakhromjonov.backend.model.User;
import com.bakhromjonov.backend.repository.UserRepository;
import com.bakhromjonov.backend.security.CurrentUser;
import com.bakhromjonov.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }
}
