package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.Customer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Sarosh
 */
@Repository
public interface CustomersRepository extends JpaRepository<Customer, String>, PagingAndSortingRepository<Customer, String> {

    Customer findByUsername(String userName);

    Customer findByUsernameOrEmail(String userName, String email);

    List<Customer> findByStoreId(String storeId);

    Customer findByUsernameAndStoreId(String username, String storeId);
}
