package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.ErrorCode;
import com.kalsym.userservice.models.daos.RegionVertical;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author 7cu
 */
@Repository
public interface ErrorCodeRepository extends PagingAndSortingRepository<ErrorCode, String>, JpaRepository<ErrorCode, String> {
    public Optional<ErrorCode> findByModulesAndErrorCategoryAndErrorCode(String modules, String errorCategory, String errorCode);
}
