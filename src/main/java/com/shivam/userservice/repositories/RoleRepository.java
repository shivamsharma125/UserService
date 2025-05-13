package com.shivam.userservice.repositories;

import com.shivam.userservice.models.Role;
import com.shivam.userservice.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByNameAndStatus(String name, Status status);
}
