package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.UserChannel;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Sarosh
 */
@Repository
public interface UserChannelsRepository extends PagingAndSortingRepository<UserChannel, String>,
        JpaRepository<UserChannel, String>,
        JpaSpecificationExecutor<UserChannel> {

    List<UserChannel> findByUserId(String userId);

    @Query("SELECT uc FROM "
            + " UserChannel uc "
            + " WHERE uc.refId = :refId ")
    Page<UserChannel> findByQuery(
            @Param("refId") String refId,
            Pageable pageable);
}
