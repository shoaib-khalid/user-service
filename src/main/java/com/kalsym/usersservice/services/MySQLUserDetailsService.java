package com.kalsym.usersservice.services;

import com.kalsym.usersservice.models.MySQLUserDetails;
import com.kalsym.usersservice.models.daos.RoleAuthority;
import com.kalsym.usersservice.models.daos.User;
import com.kalsym.usersservice.repositories.RoleAuthoritiesRepository;
import com.kalsym.usersservice.repositories.UsersRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author Sarosh
 */
@Service
public class MySQLUserDetailsService implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RoleAuthoritiesRepository roleAuthoritiesRepository;

    @Override
    public MySQLUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = usersRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        List<RoleAuthority> auths = roleAuthoritiesRepository.findByRoleId(user.getRoleId());

        return new MySQLUserDetails(user, auths);
    }
    
    

}
