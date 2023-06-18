package com.ing.hubs.services;

import com.ing.hubs.exceptions.ConstraintException;
import com.ing.hubs.models.AuthRequest;
import com.ing.hubs.models.RegisterRequest;
import com.ing.hubs.models.User;
import com.ing.hubs.repositories.UserRepository;
import com.ing.hubs.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.NonUniqueResultException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public void createUser(User user) {
        userRepository.save(user);
    }


    public void register(RegisterRequest request) throws ConstraintException {

        if(request.getLastName().matches(".*\\d.*") || request.getFirstName().matches(".*\\d.*")){
            throw new ConstraintException("Name must contain only characters");
        }
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
        try {

            userRepository.save(user);
        } catch (NonUniqueResultException ex) {
            throw new ConstraintException("User already exists! Try another username");
        }
        log.info("New user registered with username: " + user.getUsername());

    }

    public String authenticate(AuthRequest request) {
        var user = userDetailsService.loadUserByUsername(request.getUsername());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        log.info("New user logged with username: " + request.getUsername());
        return jwtService.generateToken(user);
    }


}
