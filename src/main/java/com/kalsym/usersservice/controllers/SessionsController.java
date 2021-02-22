package com.kalsym.usersservice.controllers;

import com.kalsym.usersservice.VersionHolder;
import com.kalsym.usersservice.models.AuthenticationReponse;
import com.kalsym.usersservice.models.HttpReponse;
import com.kalsym.usersservice.models.daos.Authority;
import com.kalsym.usersservice.models.daos.Client;
import com.kalsym.usersservice.models.daos.Customer;
import com.kalsym.usersservice.models.daos.Administrator;
import com.kalsym.usersservice.models.daos.ClientSession;
import com.kalsym.usersservice.models.daos.CustomerSession;
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

    @PostMapping(path = "details/client", name = "session-details-client")
    @PreAuthorize("hasAnyAuthority('session-details-client', 'all')")
    public ResponseEntity<HttpReponse> getSessionDetailsClient(HttpServletRequest request,
            @RequestBody String accessToken) throws Exception {
        String logprefix = request.getRequestURI() + " ";
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, accessToken, "");

        ClientSession session = clientSessionsRepository.findByAccessToken(accessToken);

        if (null == session) {
            Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "session not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Optional<Client> optClient = clientsRepository.findById(session.getOwnerId());

        if (!optClient.isPresent()) {
            Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "client not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        List<RoleAuthority> roleAuthories = roleAuthoritiesRepository.findByRoleId(optClient.get().getRoleId());
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
        session.setUsername(null);
        session.setId(null);

        AuthenticationReponse authReponse = new AuthenticationReponse();
        authReponse.setSession(session);
        authReponse.setAuthorities(authorities);
        authReponse.setRole(optClient.get().getRoleId());
        response.setSuccessStatus(HttpStatus.ACCEPTED);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(path = "details/customer", name = "session-details-customer")
    @PreAuthorize("hasAnyAuthority('session-details-customer', 'all')")
    public ResponseEntity<HttpReponse> getSessionDetailsCustomer(HttpServletRequest request,
            @RequestBody String accessToken) throws Exception {
        String logprefix = request.getRequestURI() + " ";
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, accessToken, "");

        CustomerSession session = customerSessionsRepository.findByAccessToken(accessToken);

        if (null == session) {
            Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "session not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Optional<Customer> optCustomer = customersRepository.findById(session.getOwnerId());

        if (!optCustomer.isPresent()) {
            Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "customer not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        List<RoleAuthority> roleAuthories = roleAuthoritiesRepository.findByRoleId(optCustomer.get().getRoleId());
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
        session.setUsername(null);
        session.setId(null);

        AuthenticationReponse authReponse = new AuthenticationReponse();
        authReponse.setSession(session);
        authReponse.setAuthorities(authorities);
        authReponse.setRole(optCustomer.get().getRoleId());
        response.setSuccessStatus(HttpStatus.ACCEPTED);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(path = "details/administrator", name = "session-details-administrator")
    @PreAuthorize("hasAnyAuthority('session-details-administrator', 'all')")
    public ResponseEntity<HttpReponse> getSessionDetailsAdministrator(HttpServletRequest request,
            @RequestBody String accessToken) throws Exception {
        String logprefix = request.getRequestURI() + " ";
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, accessToken, "");

        AdministratorSession session = administratorSessionsRepository.findByAccessToken(accessToken);

        if (null == session) {
            Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "session not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Optional<Administrator> optAdministrator = administratorsRepository.findById(session.getOwnerId());

        if (!optAdministrator.isPresent()) {
            Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "administrator not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        List<RoleAuthority> roleAuthories = roleAuthoritiesRepository.findByRoleId(optAdministrator.get().getRoleId());
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
        session.setUsername(null);
        session.setId(null);

        AuthenticationReponse authReponse = new AuthenticationReponse();
        authReponse.setSession(session);
        authReponse.setAuthorities(authorities);
        authReponse.setRole(optAdministrator.get().getRoleId());
        response.setSuccessStatus(HttpStatus.ACCEPTED);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
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
