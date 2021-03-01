package com.kalsym.usersservice.repositories;


import com.kalsym.usersservice.models.daos.UserChannel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Sarosh
 */
@Repository
public interface UserChannelsRepository extends PagingAndSortingRepository<UserChannel, String>, JpaRepository<UserChannel, String> {

    List<UserChannel> findByUserId(String userId);
}
