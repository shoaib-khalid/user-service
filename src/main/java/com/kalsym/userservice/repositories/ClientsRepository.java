package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.Client;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Sarosh
 */
@Repository
public interface ClientsRepository extends PagingAndSortingRepository<Client, String>, JpaRepository<Client, String> {

    Client findByUsername(String userName);
    
    Client findByUsernameOrEmail(String userName, String email);
  
    Client findByUsernameAndPasswordAndId(String userName, String password, String id);
    
    Client findByNameAndStoreIdAndRoleId(String name, String storeId, String roleId);
    
    @Query(value = "SELECT DISTINCT(A.id) "
            + "FROM `client` A INNER JOIN `store` B ON A.id=B.clientId  "
            + "WHERE RIGHT(A.id,1) = :suffix", nativeQuery = true)
    List<Object[]> getActiveClient(@Param("suffix") String suffix);
    
    @Transactional 
    @Modifying
    @Query("UPDATE Client m SET m.mobilePingTxnId=:transactionId WHERE m.id = :clientId")
    void UpdatePingTransactionId(
            @Param("clientId") String clientId,
            @Param("transactionId") String transactionId
            );
}
