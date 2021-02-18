package com.kalsym.usersservice.models.daos;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Sarosh
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleAuthorityIdentity implements Serializable {

    private String roleId;
    private String authorityId;

    public RoleAuthorityIdentity(String roleId, String authorityId) {
        this.roleId = roleId;
        this.authorityId = authorityId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RoleAuthorityIdentity other = (RoleAuthorityIdentity) obj;
        if (!Objects.equals(this.roleId, other.roleId)) {
            return false;
        }
        if (!Objects.equals(this.authorityId, other.authorityId)) {
            return false;
        }
        return true;
    }

}
