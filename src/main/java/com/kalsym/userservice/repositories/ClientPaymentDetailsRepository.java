package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.ClientPaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Sarosh
 */
@Repository
public interface ClientPaymentDetailsRepository extends PagingAndSortingRepository<ClientPaymentDetail, String>,
        JpaRepository<ClientPaymentDetail, String> {

}
