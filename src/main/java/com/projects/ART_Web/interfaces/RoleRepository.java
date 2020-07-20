package com.projects.ART_Web.interfaces;

import com.projects.ART_Web.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role getByName(String name);
}
