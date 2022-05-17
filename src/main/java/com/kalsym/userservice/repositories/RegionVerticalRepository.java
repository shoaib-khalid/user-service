package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.RegionVertical;
import com.kalsym.userservice.models.daos.RoleAuthority;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author 7cu
 */
@Repository
public interface RegionVerticalRepository extends PagingAndSortingRepository<RegionVertical, String>, JpaRepository<RegionVertical, String> {

    public List<RegionVertical> findByDomain(String domain);
}
