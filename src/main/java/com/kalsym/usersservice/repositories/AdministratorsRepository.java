package com.kalsym.usersservice.repositories;

import com.kalsym.usersservice.models.daos.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Sarosh
 */
@Repository
public interface AdministratorsRepository extends PagingAndSortingRepository<Administrator, String>, JpaRepository<Administrator, String> {

    Administrator findByUsername(String userName);
}
