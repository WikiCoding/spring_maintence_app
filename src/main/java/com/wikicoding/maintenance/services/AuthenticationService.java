package com.wikicoding.maintenance.services;

import com.wikicoding.maintenance.controllers.exceptions.EntryNotFoundException;
import com.wikicoding.maintenance.persistence.datamodel.RoleEnum;
import com.wikicoding.maintenance.persistence.datamodel.User;
import com.wikicoding.maintenance.persistence.repository.RoleRepository;
import com.wikicoding.maintenance.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public User register(String username, String plainTextPassword, String role) {
        if (!roleRepository.existsById(role.trim().toLowerCase().toUpperCase())) {
            log.error("Role {} not found.", role);
            throw new EntryNotFoundException("Role " + role + " not found");
        }

        User user = new User(username, passwordEncoder.encode(plainTextPassword),
                RoleEnum.valueOf(role.trim().toLowerCase().toUpperCase()));

        return userRepository.save(user);
    }

    public User authenticate(String username, String plainTextPassword) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, plainTextPassword)
        );

        return userRepository.findByUsername(username).orElseThrow();
    }
}
