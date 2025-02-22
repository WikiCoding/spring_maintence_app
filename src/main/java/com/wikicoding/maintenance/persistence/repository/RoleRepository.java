package com.wikicoding.maintenance.persistence.repository;

import com.wikicoding.maintenance.persistence.datamodel.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByRole(String role);
}
