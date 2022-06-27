package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.GuestSession;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Sarosh
 */
public interface GuestSessionsRepository extends JpaRepository<GuestSession, String> {
   
}
