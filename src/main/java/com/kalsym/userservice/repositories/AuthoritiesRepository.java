package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.Authority;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Sarosh
 */
@Repository
public interface AuthoritiesRepository extends JpaRepository<Authority, String>, PagingAndSortingRepository<Authority, String> {

}
