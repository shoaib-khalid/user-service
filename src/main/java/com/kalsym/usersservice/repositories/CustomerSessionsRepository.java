package com.kalsym.usersservice.repositories;

import com.kalsym.usersservice.models.daos.CustomerSession;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Sarosh
 */
public interface CustomerSessionsRepository extends JpaRepository<CustomerSession, String> {

    CustomerSession findByAccessToken(String accessToken);

}
