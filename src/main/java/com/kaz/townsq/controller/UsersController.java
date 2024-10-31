package com.kaz.townsq.controller;

import com.kaz.townsq.config.JwtService;
import com.kaz.townsq.dto.request.UserRequest;
import com.kaz.townsq.dto.response.UserResponse;
import com.kaz.townsq.dto.request.UserUpdateRequest;
import com.kaz.townsq.exception.InvalidRoleException;
import com.kaz.townsq.exception.UsernameAlreadyExistsException;
import com.kaz.townsq.model.User;
import com.kaz.townsq.service.CustomUserDetailsService;
import com.kaz.townsq.validation.RoleValidationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/users")
public class UsersController {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final RoleValidationService roleValidationService;

    public UsersController(CustomUserDetailsService customUserDetailsService, JwtService jwtService, RoleValidationService roleValidationService) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
        this.roleValidationService = roleValidationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserRequest userRequest) {
        try {
            roleValidationService.validateRoleToBeCreated(userRequest.getRole());
            return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromUser(customUserDetailsService.createUser(userRequest)));
        } catch (InvalidRoleException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    @GetMapping
    public ResponseEntity<UserResponse> getCurrentUser(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final String jwt = authHeader.substring(7);
        final String username = jwtService.extractUsername(jwt);

        try {

            return ResponseEntity.ok(UserResponse.fromUser(customUserDetailsService.findByUsername(username)));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNT_MANAGER')")
    public ResponseEntity<Object> updateUser(@PathVariable Long id,
                                           @RequestBody @Valid UserUpdateRequest updateRequest,
                                           Authentication authentication) {
        try {
            User existingUser = customUserDetailsService.findByUserId(id)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

            roleValidationService.validateRoleChange(
                    existingUser.getRole(),
                    updateRequest.getRole(),
                    authentication
            );
            return ResponseEntity.ok(UserResponse.fromUser(customUserDetailsService.updateUser(id, updateRequest)));
        } catch (IllegalArgumentException | InvalidRoleException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UsernameAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }

    }
}
