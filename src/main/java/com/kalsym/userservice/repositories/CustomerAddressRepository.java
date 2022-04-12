package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.CustomerAddress;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Sarosh
 */
@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, String>, PagingAndSortingRepository<CustomerAddress, String> {

    @Transactional 
    @Modifying
    @Query("UPDATE CustomerAddress m SET m.isDefault=false WHERE m.customerId = :customerId AND m.id <> :addressId") 
    void UpdateDefaultAddress(
            @Param("customerId") String customerId,
            @Param("addressId") String addressId
            );
}
