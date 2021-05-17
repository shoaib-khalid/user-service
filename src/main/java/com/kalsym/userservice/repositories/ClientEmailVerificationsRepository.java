package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.ClientEmailVerification;
import com.kalsym.userservice.models.daos.CustomerEmailVerification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Sarosh
 */
public interface ClientEmailVerificationsRepository extends JpaRepository<ClientEmailVerification, String> {
public List<ClientEmailVerification> findByClientId(String clientId);
}
