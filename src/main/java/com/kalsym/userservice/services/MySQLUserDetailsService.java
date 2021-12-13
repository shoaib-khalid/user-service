package com.kalsym.userservice.services;

import com.kalsym.userservice.UserServiceApplication;
import com.kalsym.userservice.models.MySQLUserDetails;
import com.kalsym.userservice.models.daos.Administrator;
import com.kalsym.userservice.models.daos.RoleAuthority;
import com.kalsym.userservice.models.daos.Client;
import com.kalsym.userservice.models.daos.Customer;
import com.kalsym.userservice.repositories.AdministratorsRepository;
import com.kalsym.userservice.repositories.ClientsRepository;
import com.kalsym.userservice.repositories.CustomersRepository;
import com.kalsym.userservice.repositories.RoleAuthoritiesRepository;
import com.kalsym.userservice.utils.Logger;
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

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, "", "username: " + username, "");

        Client client = clientsRepository.findByUsernameOrEmail(username, username);
        
        Customer customer = null;
        List<Customer> customerList = customersRepository.findByUsernameOrEmail(username, username);
        if (customerList.size()>1) {
            Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, "", "Customer with same email address. Found record:"+customerList.size());
        } else if (customerList.size()>0) {
            customer = customerList.get(0);
        }
        
        Administrator administrator = administratorsRepository.findByUsernameOrEmail(username, username);

        String roleId = null;
        MySQLUserDetails mud = null;
        if (null == client && null == administrator && null == customer) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        } else if (null != client) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, "", "client: " + client, "");
            roleId = client.getRoleId();
            List<RoleAuthority> auths = roleAuthoritiesRepository.findByRoleId(roleId);
            mud = new MySQLUserDetails(client, auths);
        } else if (null != administrator) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, "", "administrator: " + administrator, "");

            roleId = administrator.getRoleId();
            List<RoleAuthority> auths = roleAuthoritiesRepository.findByRoleId(roleId);
            mud = new MySQLUserDetails(administrator, auths);
        } else if (null != customer) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, "", "customer: " + customer, "");

            roleId = customer.getRoleId();
            List<RoleAuthority> auths = roleAuthoritiesRepository.findByRoleId(roleId);
            mud = new MySQLUserDetails(customer, auths);
        }

        return mud;
    }

}
