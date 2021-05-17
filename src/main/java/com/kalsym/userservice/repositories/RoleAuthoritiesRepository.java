package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.RoleAuthorityIdentity;
import com.kalsym.userservice.models.daos.RoleAuthority;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Sarosh
 */
@Repository
public interface RoleAuthoritiesRepository extends JpaRepository<RoleAuthority, RoleAuthorityIdentity>, PagingAndSortingRepository<RoleAuthority, RoleAuthorityIdentity> {

    public List<RoleAuthority> findByRoleId(String roleId);

    public List<RoleAuthority> findByRoleIdAndServiceId(String roleId, String serviceId);

}
