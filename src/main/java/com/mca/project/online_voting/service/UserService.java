package com.mca.project.online_voting.service;
import com.mca.project.online_voting.entity.Users;
import com.mca.project.online_voting.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public Users createUser(Users user) {
        // Here you can add business logic before saving:
        // - Encrypt password (highly recommended!)
        // - Set registration date if not already set
        // - Validate unique fields (userId, email, phone) before saving
        if (user.getRegdDate() == null) {
            user.setRegdDate(LocalDateTime.now());
        }
        // Example: Basic check for existing userId or email
        userRepository.findByUserId(user.getUserId()).ifPresent(u -> {
            throw new IllegalArgumentException("User with this userId already exists.");
        });
        userRepository.findByEmail(user.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("User with this email already exists.");
        });

        // In a real application, you'd use a password encoder like BCryptPasswordEncoder
        // For demonstration, not encrypting here
        // user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<Users> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Users> getUserByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public Users updateUser(Long id, Users userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setName(userDetails.getName());
            user.setPhone(userDetails.getPhone());
            user.setEmail(userDetails.getEmail());
            user.setMailVerification(userDetails.getMailVerification());
            user.setGender(userDetails.getGender());
            user.setPhoto(userDetails.getPhoto());
            // You might not want to allow updating userId or password directly through this method
            // Consider separate methods or DTOs for specific updates
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}