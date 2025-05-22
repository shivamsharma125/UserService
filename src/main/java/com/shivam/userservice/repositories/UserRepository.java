package com.shivam.userservice.repositories;

import com.shivam.userservice.models.Status;
import com.shivam.userservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmailAndStatus(String email, Status status);
    Optional<User> findByEmailAndStatus(String email, Status status);
    Optional<User> findByIdAndStatus(Long id, Status status);
}
