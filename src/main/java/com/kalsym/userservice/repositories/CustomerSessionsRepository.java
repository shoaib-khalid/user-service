package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.CustomerSession;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Sarosh
 */
public interface CustomerSessionsRepository extends JpaRepository<CustomerSession, String> {

    CustomerSession findByAccessToken(String accessToken);
    
    CustomerSession findByRefreshToken(String refreshToken);
}
