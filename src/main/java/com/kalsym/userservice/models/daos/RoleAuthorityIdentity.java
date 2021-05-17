package com.kalsym.userservice.models.daos;

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
    private String serviceId;

    public RoleAuthorityIdentity(String roleId, String authorityId, String serviceId) {
        this.roleId = roleId;
        this.authorityId = authorityId;
        this.serviceId = serviceId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.roleId);
        hash = 23 * hash + Objects.hashCode(this.authorityId);
        hash = 23 * hash + Objects.hashCode(this.serviceId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
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
        if (!Objects.equals(this.serviceId, other.serviceId)) {
            return false;
        }
        return true;
    }

}
