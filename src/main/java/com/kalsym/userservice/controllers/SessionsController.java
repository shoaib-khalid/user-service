package com.kalsym.userservice.controllers;

import com.kalsym.userservice.UserServiceApplication;
import com.kalsym.userservice.models.Auth;
import com.kalsym.userservice.models.HttpReponse;
import com.kalsym.userservice.models.daos.Client;
import com.kalsym.userservice.models.daos.Customer;
import com.kalsym.userservice.models.daos.Administrator;
import com.kalsym.userservice.models.daos.ClientSession;
import com.kalsym.userservice.models.daos.StoreUserSession;
import com.kalsym.userservice.models.daos.StoreUser;
import com.kalsym.userservice.models.daos.CustomerSession;
import com.kalsym.userservice.models.daos.Session;
import com.kalsym.userservice.models.daos.AdministratorSession;
import com.kalsym.userservice.models.daos.RoleAuthority;
import com.kalsym.userservice.repositories.AdministratorSessionsRepository;
import com.kalsym.userservice.repositories.AdministratorsRepository;
import com.kalsym.userservice.repositories.ClientSessionsRepository;
import com.kalsym.userservice.repositories.StoreUserSessionsRepository;
import com.kalsym.userservice.repositories.StoreUsersRepository;
import com.kalsym.userservice.repositories.ClientsRepository;
import com.kalsym.userservice.repositories.CustomerSessionsRepository;
import com.kalsym.userservice.repositories.CustomersRepository;
import com.kalsym.userservice.repositories.RoleAuthoritiesRepository;
import com.kalsym.userservice.utils.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Sarosh
 */
@RestController()
@RequestMapping("/sessions")
public class SessionsController {

    @Autowired
    ClientSessionsRepository clientSessionsRepository;

    @Autowired
    ClientsRepository clientsRepository;

    @Autowired
    CustomerSessionsRepository customerSessionsRepository;

    @Autowired
    CustomersRepository customersRepository;

    @Autowired
    AdministratorSessionsRepository administratorSessionsRepository;

    @Autowired
    AdministratorsRepository administratorsRepository;

    @Autowired
    RoleAuthoritiesRepository roleAuthoritiesRepository;
    
    @Autowired
    StoreUserSessionsRepository storeUserSessionsRepository;
    
    @Autowired
    StoreUsersRepository storeUserRepository;
    
    @PostMapping(path = "/details", name = "session-details-client")
    //@PreAuthorize("hasAnyAuthority('session-details-client', 'all')")
    public ResponseEntity<HttpReponse> getSessionDetailsClient(HttpServletRequest request,
            @RequestParam(required = false) String serviceId,
            @RequestBody String accessToken) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, accessToken, "");
        
        String sessionType=null;
        Session session = getClientSession(accessToken, logprefix);
        if (session==null) {
            session = getCustomerSession(accessToken, logprefix);
            if (session==null) {
                session = getAdminSession(accessToken, logprefix);
                if (session==null) {
                    session = getStoreUserSession(accessToken, logprefix);
                    if (session!=null) {
                        sessionType="WAITER";
                    }
                } else {
                    sessionType="ADMIN";
                }
            } else {
                sessionType="CUSTOMER";
            }
        } else {
            sessionType="CLIENT";
        }
        
        if (null == session) {
            Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "session not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        if (session.getOwnerId() == null) {
            Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "session owner NOT_ACCEPTABLE", "");
            response.setStatus(HttpStatus.NOT_ACCEPTABLE);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
        }
        String roleId = getRoleId(session.getOwnerId(), logprefix);
        List<RoleAuthority> roleAuthories = null;
        if (null != serviceId) {
            roleAuthories = roleAuthoritiesRepository.findByRoleIdAndServiceId(roleId, serviceId);
        } else {
            roleAuthories = roleAuthoritiesRepository.findByRoleId(roleId);
        }
        ArrayList<String> authorities = new ArrayList<>();
        if (null != roleAuthories) {
            for (RoleAuthority roleAuthority : roleAuthories) {
                authorities.add(roleAuthority.getAuthorityId());
            }
        }
        session.setUpdated(null);
        session.setStatus(null);
        session.setRemoteAddress(null);

        Auth authReponse = new Auth();
        authReponse.setSession(session);
        authReponse.setAuthorities(authorities);
        authReponse.setRole(roleId);
        authReponse.setSessionType(sessionType);
        response.setData(authReponse);
        response.setStatus(HttpStatus.ACCEPTED);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    private String getRoleId(String userId, String logprefix) {
        Optional<Client> optClient = clientsRepository.findById(userId);

        if (optClient.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client found", "");
            return optClient.get().getRoleId();
        }

        Optional<Customer> optCustomer = customersRepository.findById(userId);
        if (optCustomer.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customer found", "");
            return optCustomer.get().getRoleId();
        }
        
        Optional<StoreUser> optStoreUser = storeUserRepository.findById(userId);

        if (optStoreUser.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "StoreUser found", "");
            return optStoreUser.get().getRoleId();
        }
        
        Optional<Administrator> optAdministrator = administratorsRepository.findById(userId);

        if (optAdministrator.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "administrator found", "");
            return optAdministrator.get().getRoleId();
        }

        return null;
    }

    private Session getClientSession(String accessToken, String logprefix) {
        ClientSession clientSession = clientSessionsRepository.findByAccessToken(accessToken);

        if (null != clientSession) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client session found", "");
            return clientSession;
        }
        
        return null;
    }
    
    private Session getStoreUserSession(String accessToken, String logprefix) {
        StoreUserSession storeUserSession = storeUserSessionsRepository.findByAccessToken(accessToken);

        if (null != storeUserSession) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "storeuser session found", "");
            return storeUserSession;
        }
        
        return null;
    }
    
    private Session getCustomerSession(String accessToken, String logprefix) {
        CustomerSession customerSession = customerSessionsRepository.findByAccessToken(accessToken);

        if (null != customerSession) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customer session found", "");
            return customerSession;
        }
        
        return null;
    }

    private Session getAdminSession(String accessToken, String logprefix) {
        AdministratorSession administratorSession = administratorSessionsRepository.findByAccessToken(accessToken);

        if (null != administratorSession) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "administrator session found", "");
            return administratorSession;
        }

        return null;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity handleExceptionBadRequestException(HttpServletRequest request, MethodArgumentNotValidException e) {
        String logprefix = request.getRequestURI();
        Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "validation failed", "");
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());
        HttpReponse response = new HttpReponse(request.getRequestURI());
        response.setStatus(HttpStatus.BAD_REQUEST);
        response.setData(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
