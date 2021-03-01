package com.kalsym.usersservice.services;

import com.kalsym.usersservice.UsersServiceApplication;
import com.kalsym.usersservice.models.MySQLUserDetails;
import com.kalsym.usersservice.models.daos.Administrator;
import com.kalsym.usersservice.models.daos.RoleAuthority;
import com.kalsym.usersservice.models.daos.Client;
import com.kalsym.usersservice.models.daos.Customer;
import com.kalsym.usersservice.repositories.AdministratorsRepository;
import com.kalsym.usersservice.repositories.ClientsRepository;
import com.kalsym.usersservice.repositories.CustomersRepository;
import com.kalsym.usersservice.repositories.RoleAuthoritiesRepository;
import com.kalsym.usersservice.utils.Logger;
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
    private ClientsRepository clientsRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private AdministratorsRepository administratorsRepository;

    @Autowired
    private RoleAuthoritiesRepository roleAuthoritiesRepository;

    @Override
    public MySQLUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Client client = clientsRepository.findByUsername(username);

        Customer customer = customersRepository.findByUsername(username);

        Administrator administrator = administratorsRepository.findByUsername(username);

        String roleId = null;
        MySQLUserDetails mud = null;
        if (null == client && null == administrator && null == customer) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        } else if (null != client) {
            Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, "", "client: " + client, "");
            roleId = client.getRoleId();
            List<RoleAuthority> auths = roleAuthoritiesRepository.findByRoleId(roleId);
            mud = new MySQLUserDetails(client, auths);
        } else if (null != administrator) {
            Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, "", "administrator: " + administrator, "");

            roleId = administrator.getRoleId();
            List<RoleAuthority> auths = roleAuthoritiesRepository.findByRoleId(roleId);
            mud = new MySQLUserDetails(administrator, auths);
        } else if (null != customer) {
            Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, "", "customer: " + customer, "");

            roleId = customer.getRoleId();
            List<RoleAuthority> auths = roleAuthoritiesRepository.findByRoleId(roleId);
            mud = new MySQLUserDetails(customer, auths);
        }

        return mud;
    }

}
