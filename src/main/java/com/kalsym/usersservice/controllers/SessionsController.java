package com.kalsym.usersservice.controllers;

import com.kalsym.usersservice.VersionHolder;
import com.kalsym.usersservice.models.Auth;
import com.kalsym.usersservice.models.HttpReponse;
import com.kalsym.usersservice.models.daos.Client;
import com.kalsym.usersservice.models.daos.Customer;
import com.kalsym.usersservice.models.daos.Administrator;
import com.kalsym.usersservice.models.daos.ClientSession;
import com.kalsym.usersservice.models.daos.CustomerSession;
import com.kalsym.usersservice.models.daos.Session;
import com.kalsym.usersservice.models.daos.AdministratorSession;
import com.kalsym.usersservice.models.daos.RoleAuthority;
import com.kalsym.usersservice.repositories.AdministratorSessionsRepository;
import com.kalsym.usersservice.repositories.AdministratorsRepository;
import com.kalsym.usersservice.repositories.ClientSessionsRepository;
import com.kalsym.usersservice.repositories.ClientsRepository;
import com.kalsym.usersservice.repositories.CustomerSessionsRepository;
import com.kalsym.usersservice.repositories.CustomersRepository;
import com.kalsym.usersservice.repositories.RoleAuthoritiesRepository;
import com.kalsym.usersservice.utils.Logger;
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

    @PostMapping(path = "/details", name = "session-details-client")
    //@PreAuthorize("hasAnyAuthority('session-details-client', 'all')")
    public ResponseEntity<HttpReponse> getSessionDetailsClient(HttpServletRequest request,
            @RequestParam(required = false) String serviceId,
            @RequestBody String accessToken) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, accessToken, "");

        Session session = getSession(accessToken, logprefix);

        if (null == session) {
            Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "session not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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
        session.setOwnerId(null);
        session.setUpdated(null);
        session.setStatus(null);
        session.setRemoteAddress(null);

        Auth authReponse = new Auth();
        authReponse.setSession(session);
        authReponse.setAuthorities(authorities);
        authReponse.setRole(roleId);
        response.setData(authReponse);
        response.setSuccessStatus(HttpStatus.ACCEPTED);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    private String getRoleId(String userId, String logprefix) {
        Optional<Client> optClient = clientsRepository.findById(userId);

        if (optClient.isPresent()) {
            Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "client found", "");
            return optClient.get().getRoleId();
        }

        Optional<Customer> optCustomer = customersRepository.findById(userId);
        if (optCustomer.isPresent()) {
            Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "customer found", "");
            return optCustomer.get().getRoleId();
        }

        Optional<Administrator> optAdministrator = administratorsRepository.findById(userId);

        if (!optAdministrator.isPresent()) {
            Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "administrator found", "");
            return optAdministrator.get().getRoleId();
        }

        return null;
    }

    private Session getSession(String accessToken, String logprefix) {
        ClientSession clientSession = clientSessionsRepository.findByAccessToken(accessToken);

        if (null != clientSession) {
            Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "client session found", "");
            return clientSession;
        }

        CustomerSession customerSession = customerSessionsRepository.findByAccessToken(accessToken);

        if (null != customerSession) {
            Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "customer session found", "");
            return customerSession;
        }

        AdministratorSession administratorSession = administratorSessionsRepository.findByAccessToken(accessToken);

        if (null != administratorSession) {
            Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "administrator session found", "");
            return administratorSession;
        }

        return null;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity handleExceptionBadRequestException(HttpServletRequest request, MethodArgumentNotValidException e) {
        String logprefix = request.getRequestURI() + " ";
        Logger.application.warn(Logger.pattern, VersionHolder.VERSION, logprefix, "validation failed", "");
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());
        HttpReponse response = new HttpReponse(request.getRequestURI());
        response.setErrorStatus(HttpStatus.BAD_REQUEST);
        response.setData(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
