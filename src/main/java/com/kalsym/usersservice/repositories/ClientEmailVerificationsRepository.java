package com.kalsym.usersservice.repositories;

import com.kalsym.usersservice.models.daos.ClientEmailVerification;
import com.kalsym.usersservice.models.daos.CustomerEmailVerification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Sarosh
 */
public interface ClientEmailVerificationsRepository extends JpaRepository<ClientEmailVerification, String> {
public List<ClientEmailVerification> findByClientId(String clientId);
}
