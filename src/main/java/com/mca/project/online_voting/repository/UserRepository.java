package com.mca.project.online_voting.repository;
import com.mca.project.online_voting.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    // Custom query method to find a user by their userId
    Optional<Users> findByUserId(String userId);

    // Custom query method to find a user by their email
    Optional<Users> findByEmail(String email);

    // You can add more custom query methods as needed,
    // e.g., Optional<User> findByPhone(String phone);
}