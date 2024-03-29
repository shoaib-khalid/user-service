package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
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
}
