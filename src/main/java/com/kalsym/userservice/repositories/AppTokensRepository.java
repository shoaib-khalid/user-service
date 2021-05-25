package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.AppToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author 7cu
 */
@Repository
public interface AppTokensRepository extends PagingAndSortingRepository<AppToken, String>, JpaRepository<AppToken, String> {
}
