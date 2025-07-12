package com.mca.project.online_voting.service;
import com.mca.project.online_voting.entity.Company;
import com.mca.project.online_voting.entity.UserRole;
import com.mca.project.online_voting.entity.Users;
import com.mca.project.online_voting.repository.CompanyRepository;
import com.mca.project.online_voting.repository.UserRepository;
import com.mca.project.online_voting.repository.UserRoleRepository;
import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository; // New
    @Autowired
    private UserRoleRepository userRoleRepository; // New
    @Autowired
    private PasswordEncoder passwordEncoder; // New, to encode temp passwords
    @Autowired(required = false)
    private JavaMailSender mailSender;

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

    public Optional<Users> findByEmail(String email){
        return  userRepository.findByEmail(email);
    }









//    @Transactional
//    public Users createUserAndAssignRole(String email) {
//        // 1. Find or Create User
//        Users user = userRepository.findByEmail(email).orElseGet(() -> {
//            String tempPassword = generateTemporaryPassword();
//            String encodedPassword = passwordEncoder.encode(tempPassword);
//
//            Users u = Users.builder()
//                    .name(email)
//                    .password(tempPassword)
//                    .phone("")
//                    .mailVerification(true)
//                    .email(email)
//                    .userId(email)
//                    .gender(null)
//                    .photo(null)
//                    .regdDate(LocalDateTime.now())
//                    .build();
//
//            Users savedUser = userRepository.save(u);
//            sendWelcomeEmail(email, tempPassword); // Send email with temp password
//            return savedUser;
//        });
//
//        // 2. Find or Create Company
//        Company company = companyRepository.findByName(companyName).orElseGet(() -> {
//            Company newCompany = new Company();
//            newCompany.setName(companyName);
//            return companyRepository.save(newCompany);
//        });
//
//        // 3. Assign Role to User in Company
//        UserRole userRole = new UserRole(user.getId(), company.getId(), "ROLE_" + role.toUpperCase());
//        userRoleRepository.save(userRole);
//
//        // Ensure user's roles collection is updated in the current session
//        //user.getUserRoles().add(userRole);
//
//        return user;
//    }

    public String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public void sendWelcomeEmail(String toEmail, String temporaryPassword) {
        if (mailSender == null) {
            System.err.println("Mail sender not configured. Cannot send email to " + toEmail);
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your_email@example.com"); // Configure in application.yml
        message.setTo(toEmail);
        message.setSubject("Welcome to Our Application!");
        message.setText("Dear " + toEmail + ",\n\n" +
                "Welcome! Your account has been created.\n" +
                "Your temporary password is: " + temporaryPassword + "\n" +
                "Please login here: http://localhost:3000/user-login \n\n" + // Updated to /user-login
                "For security, please change your password after logging in.\n\n" +
                "Regards,\nYour App Team");
        mailSender.send(message);
    }

    public List<Users> findAllUsers() {
        return userRepository.findAll();
    }


    public long count() {
       return userRepository.count();
    }
}