package com.kalsym.userservice.models.daos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author Sarosh
 */
@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class Customer implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    private String username;

    private String password;

    private String name;

    private String email;

    private String facebookId;
    private String phoneNumber;

    private Boolean locked;
    private Boolean deactivated;
    private Boolean isActivated;
    
    private String countryId;
    
    Date created;
    Date updated;

    @NotBlank(message = "role is required")
    private String roleId;

    private String storeId;
        
    private String domain;
    
    private String channel;   
    
    private String originalUsername;
    
    private String originalEmail;
        
    public void update(Customer user) {
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

        if (null != user.getName()) {
            phoneNumber = user.getPhoneNumber();
        }

        if (null != user.getLocked()) {
            locked = user.getLocked();
        }
        
        if (null != user.getIsActivated()) {
            isActivated = user.getIsActivated();
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
        final Customer other = (Customer) obj;
        return Objects.equals(this.id, other.getId());
    }

    @Override
    public String toString() {
        return "UserEntity{" + "id=" + id + ", username=" + username + ", password=" + password + ", name=" + name + ", email=" + email + ", locked=" + locked + ", deactivated=" + deactivated + ", created=" + created + ", updated=" + updated + ", roleId=" + roleId + '}';
    }
}
