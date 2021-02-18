package com.kalsym.usersservice.models;

import com.kalsym.usersservice.models.daos.RoleAuthority;
import com.kalsym.usersservice.models.daos.Client;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author user
 */
@Getter
@Setter
@ToString
public class UserProfile {

    private Client userProfile;
    List<RoleAuthority> roleList;

    private List<GrantedAuthority> grantedAuthorities;

    public UserProfile() {
    }

    public UserProfile(Client user, List<RoleAuthority> auths) {
        this.userProfile = user;
        this.roleList = auths;
    }

}
