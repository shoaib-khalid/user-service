package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.CustomerEmailVerification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Sarosh
 */
public interface CustomerEmailVerificationsRepository extends JpaRepository<CustomerEmailVerification, String> {

    public List<CustomerEmailVerification> findByCustomerId(String customerId);
}
