package com.douyingroup.IMS.repository;

import com.douyingroup.IMS.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    // Add any custom methods if needed
}