package com.aajumaharjan.demofeatures.auth.repository;

import com.aajumaharjan.demofeatures.auth.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByNameIn(Collection<String> names);
}
