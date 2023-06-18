package com.ing.hubs.controllers;

import com.ing.hubs.exceptions.ConstraintException;
import com.ing.hubs.models.AuthRequest;
import com.ing.hubs.models.RegisterRequest;
import com.ing.hubs.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("app/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) throws ConstraintException {
        userService.register(request);
        return ResponseEntity.ok("Successfully registered " + request.getUsername());
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticate(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(userService.authenticate(authRequest));

    }
}
