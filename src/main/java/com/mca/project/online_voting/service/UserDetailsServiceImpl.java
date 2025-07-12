package com.mca.project.online_voting.service;

import com.mca.project.online_voting.entity.Users;
import com.mca.project.online_voting.entity.UserRole; // Import UserRole
import com.mca.project.online_voting.repository.UserRepository;
import com.mca.project.online_voting.repository.UserRoleRepository; // New Import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List; // For List<UserRole>
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository; // Inject UserRoleRepository

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Fetch roles explicitly from UserRoleRepository
        List<UserRole> userRoles = userRoleRepository.findByIdUserId(user.getId());

        Collection<? extends GrantedAuthority> authorities =
                userRoles.stream() // Use the fetched list of UserRole objects
                        .map(userRole -> new SimpleGrantedAuthority(userRole.getRole()))
                        .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
