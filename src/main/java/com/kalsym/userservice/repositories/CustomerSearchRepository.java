package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.CustomerSearchHistory;
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
public interface CustomerSearchRepository extends JpaRepository<CustomerSearchHistory, String>, PagingAndSortingRepository<CustomerSearchHistory, String> {

   
}
