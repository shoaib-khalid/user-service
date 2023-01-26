package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.StoreUser;
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
public interface StoreUsersRepository extends PagingAndSortingRepository<StoreUser, String>, JpaRepository<StoreUser, String> {

    StoreUser findByUsername(String username);
    
    StoreUser findByUsernameOrEmail(String userName, String email);
    
    List<StoreUser> findByStoreId(String storeId);

    StoreUser findByUsernameAndStoreId(String username, String storeId);
    
    @Transactional 
    @Modifying
    @Query("UPDATE StoreUser m SET m.fcmToken=:newToken WHERE m.id = :userId")
    void UpdateFcmToken(
            @Param("userId") String userId,
            @Param("newToken") String newToken
            );
}
