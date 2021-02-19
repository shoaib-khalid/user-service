package com.kalsym.usersservice.repositories;

import com.kalsym.usersservice.models.daos.AdministratorSession;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Sarosh
 */
public interface AdministratorSessionsRepository extends JpaRepository<AdministratorSession, String> {

}
