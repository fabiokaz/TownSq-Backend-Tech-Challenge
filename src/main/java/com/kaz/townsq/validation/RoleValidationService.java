package com.kaz.townsq.validation;

import com.kaz.townsq.exception.InvalidRoleException;
import com.kaz.townsq.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class RoleValidationService {

    public void validateRoleChange(Role currentRole, String newRole, Authentication authentication) {
        Role targetRole;
        try {
            targetRole = Role.fromString(newRole.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleException("Invalid role: " + newRole +
                    ". Valid roles are: " + Arrays.toString(Role.values()));
        }

        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        boolean isAccountManager = hasRole(authentication, "ROLE_ACCOUNT_MANAGER");

        if (!isAdmin) {
            if (isAccountManager) {
                validateAccountManagerRoleChange(currentRole, targetRole);
            } else {
                throw new AccessDeniedException("You don't have permission to change roles");
            }
        }
    }

    private void validateAccountManagerRoleChange(Role currentRole, Role targetRole) {
        if (currentRole == Role.ADMIN) {
            throw new AccessDeniedException("Account managers can only modify users with ACCOUNT_MANAGER or DEFAULT role");
        }
        if (targetRole == Role.ADMIN) {
            throw new AccessDeniedException("Account Managers can only assign ACCOUNT_MANAGER or DEFAULT role");
        }
    }

    public void validateRoleToBeCreated(String role) {
        try {
            Role.fromString(role.toUpperCase());
            if (role.equals(Role.ADMIN.name()) || role.equals(Role.ACCOUNT_MANAGER.name())) {
                throw new InvalidRoleException("Invalid role: " + role +
                        ". Valid role is only: " + Role.DEFAULT.name());
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleException("Invalid role: " + role +
                    ". Valid role is only: " + Role.DEFAULT.name());
        }
    }

    public void validateRoleToBeUpdated(String role) {
        try {
            Role.fromString(role.toUpperCase());
            if (role.equals(Role.ADMIN.name())) {
                throw new InvalidRoleException("Invalid role: " + role +
                        ". Valid role is: " + Role.DEFAULT.name() + " or " + Role.ACCOUNT_MANAGER.name());
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleException("Invalid role: " + role +
                    ". Valid role is: " + Role.DEFAULT.name() + " or " + Role.ACCOUNT_MANAGER.name());
        }
    }

    public void validateRoleToProcessPayments(Authentication authentication) {
            boolean isAccountManager = hasRole(authentication, "ROLE_ACCOUNT_MANAGER");
            if (!isAccountManager) {
                throw new InvalidRoleException("Invalid role! The valid role is: " + Role.ACCOUNT_MANAGER);
            }
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }
}
