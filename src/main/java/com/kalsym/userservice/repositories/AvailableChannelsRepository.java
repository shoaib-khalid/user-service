package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.AvailableChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Sarosh
 */
@Repository
public interface AvailableChannelsRepository extends PagingAndSortingRepository<AvailableChannel, String>, JpaRepository<AvailableChannel, String> {

}
