package com.kalsym.usersservice.repositories;

import com.kalsym.usersservice.models.daos.Session;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Sarosh
 */
public interface SessionsRepository extends JpaRepository<Session, String> {

}
