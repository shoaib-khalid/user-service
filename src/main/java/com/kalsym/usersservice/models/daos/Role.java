package com.kalsym.usersservice.models.daos;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Sarosh
 */
@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class Role implements Serializable {

    @Id
    @Column(name = "id")
    @NotBlank(message = "id is required")
    private String id;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "simoultaneousLogins is required")
    private Integer allowedSimoultaneousSessions;

    private String description;

    private String parentRoleId;

    public void update(Role role) {
        if (null != role.getId()) {
            id = role.getId();
        }

        if (null != role.getDescription()) {
            description = role.getDescription();
        }

        if (0 != role.getAllowedSimoultaneousSessions()) {
            allowedSimoultaneousSessions = role.getAllowedSimoultaneousSessions();
        }

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Role other = (Role) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

   

}
