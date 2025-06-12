package com.keepsafe.notes.repositories;

import com.keepsafe.notes.models.AppRole;
import com.keepsafe.notes.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
    Optional<Role> findByRoleName(AppRole appRole);
}
