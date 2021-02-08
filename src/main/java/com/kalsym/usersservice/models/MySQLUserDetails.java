package com.kalsym.usersservice.models;


import com.kalsym.usersservice.models.daos.RoleAuthority;
import com.kalsym.usersservice.models.daos.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author Sarosh
 */
public class MySQLUserDetails implements UserDetails {

    private String userName;
    private String password;
    private boolean locked;
    private boolean expired;
    private String role;

    private List<GrantedAuthority> grantedAuthorities;

    public MySQLUserDetails() {
    }

    public MySQLUserDetails(String username) {
        this.userName = username;
    }

    public MySQLUserDetails(User user, List auths) {
        this.userName = user.getUsername();
        this.password = user.getPassword();
        this.locked = user.getLocked();
        this.expired = user.getDeactivated();
        this.role = user.getRoleId();
        grantedAuthorities = new ArrayList<>();

        List<RoleAuthority> userAuths = auths;
        userAuths.stream().forEach((userAuth) -> {
            grantedAuthorities.add(new SimpleGrantedAuthority(userAuth.getAuthorityId()));
        });
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !this.expired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !this.locked;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
