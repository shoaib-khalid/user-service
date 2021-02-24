package com.kalsym.usersservice.repositories;

import com.kalsym.usersservice.models.daos.ClientSession;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Sarosh
 */
public interface ClientSessionsRepository extends JpaRepository<ClientSession, String> {
    ClientSession findByAccessToken(String accessToken);

    ClientSession findByRefreshToken(String accessToken);

}
