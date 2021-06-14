package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.ClientPasswordReset;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Sarosh
 */
public interface ClientPasswordResetsRepository extends JpaRepository<ClientPasswordReset, String> {

    public List<ClientPasswordReset> findByClientId(String clientId);
}
