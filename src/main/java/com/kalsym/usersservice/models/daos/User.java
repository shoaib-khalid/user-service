package com.kalsym.usersservice.models.daos;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author Sarosh
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonIgnoreProperties(value = {"password"})
public class User implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @NotBlank(message = "username is required")
    private String username;

    @JsonProperty(access = Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "email is required")
    private String email;

    private Boolean locked;
    private Boolean deactivated;
    String created;
    String updated;
    
    //FOR QUERY DATA
    @OneToOne()
    @JoinColumn(name="id", insertable=false, updatable=false)
    private Customer customer;
    
    @NotBlank(message = "role is required")
    private String roleId;

    public void updateUser(User user) {
        if (null != user.getEmail()) {
            email = user.getEmail();
        }

        if (null != user.getDeactivated()) {
            deactivated = user.getDeactivated();
        }

        if (null != user.getRoleId()) {
            roleId = user.getRoleId();
        }

        if (null != user.getUsername()) {
            username = user.getUsername();
        }

        if (null != user.getPassword()) {
            password = user.getPassword();
        }

        if (null != user.getName()) {
            name = user.getName();
        }

        if (null != user.getLocked()) {
            locked = user.getLocked();
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
        final User other = (User) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", username=" + username + ", password=" + password + ", name=" + name + ", email=" + email + ", locked=" + locked + ", deactivated=" + deactivated + ", created=" + created + ", updated=" + updated + ", roleId=" + roleId + '}';
    }

}
