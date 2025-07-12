package com.mca.project.online_voting.controller;
import com.mca.project.online_voting.entity.Company;
import com.mca.project.online_voting.entity.UserRole;
import com.mca.project.online_voting.entity.Users;
import com.mca.project.online_voting.repository.UserRoleRepository;
import com.mca.project.online_voting.service.CompanyService;
import com.mca.project.online_voting.service.UserService;
import com.mca.project.online_voting.service.UserRoleService;
import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users") // Base path for all user-related endpoints
public class UserController {

    private final UserService userService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private UserRoleRepository userRoleRepository;
    private final UserRoleService userRoleService;

    @Autowired
    public UserController(UserService userService, UserRoleService userRoleService) {
        this.userService = userService;
        this.userRoleService = userRoleService;
    }

    @PostMapping("/createUser")
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        try {
            Users createdUser = userService.createUser(user);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Handle cases where user already exists or other business validation fails
            return new ResponseEntity<>(null, HttpStatus.CONFLICT); // Or HttpStatus.BAD_REQUEST
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/userid/{userId}")
    public ResponseEntity<Users> getUserByUserId(@PathVariable String userId) {
        return userService.getUserByUserId(userId)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User oauth2User, Principal principal) {
        Map<String, Object> userDetails = new HashMap<>();

        if (oauth2User != null) { // OAuth2 authenticated user

            userDetails.putAll(oauth2User.getAttributes());
            Optional<Users> user =  userService.findByEmail(oauth2User.getAttribute("email"));

            if(!user.isEmpty())
                userDetails.put("userId", user.get().getId());
            else
                userDetails.put("userId", 0);

            userDetails.put("roles", SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
        } else if (principal != null) { // Basic/Form authenticated user
            String email = principal.getName();
            userService.findByEmail(email).ifPresent(user -> {
                userDetails.put("name", user.getEmail());
                userDetails.put("email", user.getEmail());
                // Fetch roles explicitly for this user
                List<UserRole> userRoles = userRoleService.findByUserId(user.getId());
                userDetails.put("roles", userRoles.stream()
                        .map(r -> r.getRole())
                        .collect(Collectors.toList()));
            });
        }
        return userDetails;
    }




    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Users> updateUser(@PathVariable Long id, @RequestBody Users userDetails) {
        try {
            Users updatedUser = userService.updateUser(id, userDetails);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // User not found
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content for successful deletion
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getUserRoles(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }
}