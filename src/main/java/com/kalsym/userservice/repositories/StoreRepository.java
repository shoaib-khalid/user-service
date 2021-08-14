package com.kalsym.userservice.repositories;

import java.util.List;
import com.kalsym.userservice.models.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author 7cu
 */
@Repository
public interface StoreRepository extends PagingAndSortingRepository<Store, String>, JpaRepository<Store, String> {

    

}
