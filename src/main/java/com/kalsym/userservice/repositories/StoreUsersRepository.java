package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.StoreUser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Sarosh
 */
@Repository
public interface StoreUsersRepository extends PagingAndSortingRepository<StoreUser, String>, JpaRepository<StoreUser, String> {

    StoreUser findByUsername(String username);
    
    StoreUser findByUsernameOrEmail(String userName, String email);
    
    List<StoreUser> findByStoreId(String storeId);

    StoreUser findByUsernameAndStoreId(String username, String storeId);
}
