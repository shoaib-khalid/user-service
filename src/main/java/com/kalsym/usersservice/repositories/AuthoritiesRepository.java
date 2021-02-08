package com.kalsym.usersservice.repositories;

import com.kalsym.usersservice.models.daos.Authority;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Sarosh
 */
@Repository
public interface AuthoritiesRepository extends JpaRepository<Authority, String> {

    public List<Authority> findByServiceId(String serviceId);
}
