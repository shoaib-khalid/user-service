package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.ApplicationError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author 7cu
 */
@Repository
public interface ApplicationErrorRepository extends PagingAndSortingRepository<ApplicationError, String>, JpaRepository<ApplicationError, String> {
}
