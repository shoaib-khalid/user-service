package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.AdministratorSession;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Sarosh
 */
public interface AdministratorSessionsRepository extends JpaRepository<AdministratorSession, String> {

    AdministratorSession findByAccessToken(String accessToken);

}
