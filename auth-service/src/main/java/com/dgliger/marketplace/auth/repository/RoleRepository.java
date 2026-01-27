package com.dgliger.marketplace.auth.repository;


import com.dgliger.marketplace.auth.entity.Role;
import com.dgliger.marketplace.auth.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
}