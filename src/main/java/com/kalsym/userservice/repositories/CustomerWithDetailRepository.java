package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.CustomerWithDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Sarosh
 */
@Repository
public interface CustomerWithDetailRepository extends JpaRepository<CustomerWithDetail, String>,
        PagingAndSortingRepository<CustomerWithDetail, String> {

    CustomerWithDetail findByUsername(String userName);

    CustomerWithDetail findByUsernameOrEmail(String userName, String email);

    List<CustomerWithDetail> findByStoreId(String storeId);

    CustomerWithDetail findByUsernameAndStoreId(String username, String storeId);
}
