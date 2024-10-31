package com.kaz.townsq.service;

import com.kaz.townsq.dto.request.UserRequest;
import com.kaz.townsq.dto.request.UserUpdateRequest;
import com.kaz.townsq.exception.InvalidRoleException;
import com.kaz.townsq.exception.UserNotFoundException;
import com.kaz.townsq.exception.UsernameAlreadyExistsException;
import com.kaz.townsq.model.Role;
import com.kaz.townsq.model.User;
import com.kaz.townsq.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;


    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(user.toGrantedAuthority())
        );
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(UserRequest request) {
        try {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                throw new UsernameAlreadyExistsException("Username already exists");
            }
            User user = new User();
            user.setUsername(request.getUsername());
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(Role.fromString(request.getRole().toUpperCase()));
            return userRepository.save(user);
        } catch (InvalidRoleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error creating user", e);
        }
    }

    public User updateUser(Long id, UserUpdateRequest updatedUser) {
        try {

            return userRepository.findByUserId(id)
                    .map(existingUser -> {
                        if (!existingUser.getUsername().equals(updatedUser.getUsername())) {
                            if (userRepository.existsByUsernameAndUserIdNot(updatedUser.getUsername(), id)) {
                                throw new UsernameAlreadyExistsException("Username already taken: " + updatedUser.getUsername());
                            }
                        }
                        existingUser.setUsername(updatedUser.getUsername());
                        existingUser.setRole(Role.fromString(updatedUser.getRole()));
                        return userRepository.save(existingUser);
                    })
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        } catch (InvalidRoleException | UsernameAlreadyExistsException | UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error updating user", e);
        }
    }

    public Optional<User> findByUserId(Long id) {
        return userRepository.findByUserId(id);
    }
}
