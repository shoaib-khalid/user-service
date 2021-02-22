package com.kalsym.usersservice.controllers;

import com.kalsym.usersservice.VersionHolder;
import com.kalsym.usersservice.models.AuthenticationReponse;
import com.kalsym.usersservice.models.HttpReponse;
import com.kalsym.usersservice.models.daos.RoleAuthority;
import com.kalsym.usersservice.models.daos.ClientSession;
import com.kalsym.usersservice.models.daos.Client;
import com.kalsym.usersservice.models.requestbodies.AuthenticationBody;
import com.kalsym.usersservice.repositories.RoleAuthoritiesRepository;
import com.kalsym.usersservice.repositories.ClientSessionsRepository;
import com.kalsym.usersservice.repositories.ClientsRepository;
import com.kalsym.usersservice.utils.DateTimeUtil;
import com.kalsym.usersservice.utils.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@RequestMapping("/clients")
public class ClientsController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    ClientsRepository clientsRepository;

    @Autowired
    RoleAuthoritiesRepository roleAuthoritiesRepository;

    @Autowired
    ClientSessionsRepository clientSessionsRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Value("${session.expiry:3600}")
    private int expiry;

    @GetMapping(path = {"/"}, name = "clients-get")
    @PreAuthorize("hasAnyAuthority('clients-get', 'all')")
    public ResponseEntity<HttpReponse> getClients(HttpServletRequest request,
            @RequestParam(required = false) String clientname,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String roleId,
            @RequestParam(required = false) Boolean locked,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "", "");

        Client client = new Client();
        client.setUsername(clientname);
        client.setEmail(email);
        client.setRoleId(roleId);
        client.setLocked(locked);

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, client + "", "");
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withIgnorePaths("locked")
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Client> example = Example.of(client, matcher);

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "page: " + page + " pageSize: " + pageSize, "");
        Pageable pageable = PageRequest.of(page, pageSize);

        response.setSuccessStatus(HttpStatus.OK);
        response.setData(clientsRepository.findAll(example, pageable));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = {"/{id}"}, name = "clients-get-by-id")
    @PreAuthorize("hasAnyAuthority('clients-get-by-id', 'all')")
    public ResponseEntity<HttpReponse> getClientById(HttpServletRequest request, @PathVariable String id) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "", "");

        Optional<Client> optClient = clientsRepository.findById(id);

        if (!optClient.isPresent()) {
            Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "client not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "client found", "");
        response.setSuccessStatus(HttpStatus.OK);
        response.setData(optClient.get());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(path = {"/{id}"}, name = "clients-delete-by-id")
    @PreAuthorize("hasAnyAuthority('clients-delete-by-id', 'all')")
    public ResponseEntity<HttpReponse> deleteClientById(HttpServletRequest request, @PathVariable String id) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "", "");

        Optional<Client> optClient = clientsRepository.findById(id);

        if (!optClient.isPresent()) {
            Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "client not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "client found", "");
        clientsRepository.delete(optClient.get());

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "client deleted", "");
        response.setSuccessStatus(HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping(path = {"/{id}"}, name = "clients-put-by-id")
    @PreAuthorize("hasAnyAuthority('clients-put-by-id', 'all')")
    public ResponseEntity<HttpReponse> putClientById(HttpServletRequest request, @PathVariable String id, @RequestBody Client body) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, body.toString(), "");

        Optional<Client> optClient = clientsRepository.findById(id);

        if (!optClient.isPresent()) {
            Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "client not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "client found", "");
        Client client = optClient.get();
        List<String> errors = new ArrayList<>();

        List<Client> clients = clientsRepository.findAll();

        for (Client existingClient : clients) {
            if (!client.equals(existingClient)) {
                if (existingClient.getUsername().equals(body.getUsername())) {
                    Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "clientname already exists", "");
                    response.setErrorStatus(HttpStatus.CONFLICT);
                    errors.add("clientname already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
                if (existingClient.getEmail().equals(body.getEmail())) {
                    Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "email already exists", "");
                    response.setErrorStatus(HttpStatus.CONFLICT);
                    errors.add("email already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
                if (null != body.getId()) {
                    if (existingClient.getEmail().equals(body.getEmail())) {
                        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "clientId already exists", "");
                        response.setErrorStatus(HttpStatus.CONFLICT);
                        errors.add("clientId already exists");
                        response.setData(errors);
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                    }
                }
            }

        }

        if (null != body.getPassword() && body.getPassword().length() > 0) {
            body.setPassword(bcryptEncoder.encode(body.getPassword()));
        } else {
            body.setPassword(null);
        }

        client.update(body);
        client.setUpdated(DateTimeUtil.currentTimestamp());

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "client updated for id: " + id, "");
        response.setSuccessStatus(HttpStatus.ACCEPTED);
        response.setData(clientsRepository.save(client));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(path = "/register", name = "clients-post")
    //@PreAuthorize("hasAnyAuthority('clients-post', 'all')")
    public ResponseEntity<HttpReponse> postClient(HttpServletRequest request, @Valid @RequestBody Client body) throws Exception {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, body.toString(), "");

        List<String> errors = new ArrayList<>();
        if (null == body.getPassword() || body.getPassword().length() == 0) {
            Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "clientname already exists", "");
            response.setErrorStatus(HttpStatus.BAD_REQUEST);
            errors.add("password is required exists");
            response.setData(errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        List<Client> clients = clientsRepository.findAll();

        for (Client existingClient : clients) {
            if (existingClient.getUsername().equals(body.getUsername())) {
                Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "clientname already exists", "");
                response.setErrorStatus(HttpStatus.CONFLICT);
                errors.add("clientname already exists");
                response.setData(errors);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            if (existingClient.getEmail().equals(body.getEmail())) {
                Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "email already exists", "");
                response.setErrorStatus(HttpStatus.CONFLICT);
                errors.add("email already exists");
                response.setData(errors);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        }

        body.setPassword(bcryptEncoder.encode(body.getPassword()));
        body.setCreated(DateTimeUtil.currentTimestamp());
        body.setUpdated(DateTimeUtil.currentTimestamp());
        body.setLocked(false);
        body = clientsRepository.save(body);
        body.setPassword(null);
        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "client created with id: " + body.getId(), "");
        response.setSuccessStatus(HttpStatus.CREATED);
        response.setData(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //authentication
    @PostMapping(path = "/authenticate", name = "clients-authenticate")
    public ResponseEntity authenticateClient(@Valid @RequestBody AuthenticationBody body,
            HttpServletRequest request) throws Exception {
        String logprefix = request.getRequestURI() + " ";
        HttpReponse response = new HttpReponse(request.getRequestURI());
        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "body: " + body);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(body.getUsername(), body.getPassword())
            );
        } catch (BadCredentialsException e) {
            Logger.application.error(Logger.pattern, VersionHolder.VERSION, logprefix, "error validating client Bad Credentiails", e);
            response.setErrorStatus(HttpStatus.UNAUTHORIZED, "Bad Credentiails");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (AuthenticationException e) {
            Logger.application.error(Logger.pattern, VersionHolder.VERSION, logprefix, "error validating client " + e.getMessage(), e);
            response.setErrorStatus(HttpStatus.UNAUTHORIZED, e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "client authenticated", "");

        Client client = clientsRepository.findByUsername(body.getUsername());

        List<RoleAuthority> roleAuthories = roleAuthoritiesRepository.findByRoleId(client.getRoleId());
        ArrayList<String> authorities = new ArrayList<>();
        if (null != roleAuthories) {

            for (RoleAuthority roleAuthority : roleAuthories) {
                authorities.add(roleAuthority.getAuthorityId());
            }
        }

        ClientSession session = new ClientSession();
        session.setRemoteAddress(request.getRemoteAddr());
        session.setOwnerId(client.getId());
        session.setUsername(client.getUsername());
        session.setCreated(DateTimeUtil.currentTimestamp());
        session.setUpdated(DateTimeUtil.currentTimestamp());
        session.setExpiry(DateTimeUtil.expiryTimestamp(expiry));
        session.setStatus("ACTIVE");
        session.generateTokens();

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "session: " + session, "");

        session = clientSessionsRepository.save(session);
        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "session created with id: " + session.getId(), "");

        session.setOwnerId(null);
        session.setUpdated(null);
        session.setStatus(null);
        session.setRemoteAddress(null);
        session.setUsername(null);
        session.setId(null);

        AuthenticationReponse authReponse = new AuthenticationReponse();
        authReponse.setSession(session);
        authReponse.setAuthorities(authorities);
        authReponse.setRole(client.getRoleId());

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "generated token", "");

        response.setSuccessStatus(HttpStatus.ACCEPTED);
        response.setData(authReponse);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
    
     @PostMapping(path = "client/details", name = "clients-session-details")
    @PreAuthorize("hasAnyAuthority('session-setails', 'all')")
    public ResponseEntity<HttpReponse> getSessionDetails(HttpServletRequest request,
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


    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity handleExceptionBadRequestException(HttpServletRequest request, MethodArgumentNotValidException e) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
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
