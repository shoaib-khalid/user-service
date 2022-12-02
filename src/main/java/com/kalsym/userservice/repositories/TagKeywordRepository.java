package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.TagKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Sarosh
 */
@Repository
public interface TagKeywordRepository extends PagingAndSortingRepository<TagKeyword, String>, JpaRepository<TagKeyword, String> {
    TagKeyword findByKeyword(String keyword);
}
