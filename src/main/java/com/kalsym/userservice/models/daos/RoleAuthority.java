package com.kalsym.userservice.models.daos;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Sarosh
 */
@Entity
@Table(name = "role_authority")
@IdClass(RoleAuthorityIdentity.class)
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleAuthority implements Serializable {

    @Id
    @NotBlank(message = "roleId is required")
    private String roleId;
    @Id
    @NotBlank(message = "authorityId is required")
    private String authorityId;
    
    @Id
    @NotBlank(message = "serviceId is required")
    private String serviceId;
}
