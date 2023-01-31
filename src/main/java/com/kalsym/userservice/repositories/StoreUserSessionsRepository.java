package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.StoreUserSession;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Sarosh
 */
public interface StoreUserSessionsRepository extends JpaRepository<StoreUserSession, String> {

    StoreUserSession findByAccessToken(String accessToken);
    
    StoreUserSession findByRefreshToken(String refreshToken);
}
