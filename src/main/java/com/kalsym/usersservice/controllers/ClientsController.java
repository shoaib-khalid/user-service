package com.kalsym.usersservice.controllers;

import com.kalsym.usersservice.models.storeagent.*;
import com.kalsym.usersservice.UsersServiceApplication;
import com.kalsym.usersservice.models.Auth;
import com.kalsym.usersservice.models.HttpReponse;
import com.kalsym.usersservice.models.daos.RoleAuthority;
import com.kalsym.usersservice.models.daos.ClientSession;
import com.kalsym.usersservice.models.daos.Client;
import com.kalsym.usersservice.models.requestbodies.AuthenticationBody;
import com.kalsym.usersservice.repositories.RoleAuthoritiesRepository;
import com.kalsym.usersservice.repositories.ClientSessionsRepository;
import com.kalsym.usersservice.repositories.ClientsRepository;
import com.kalsym.usersservice.services.EmaiVerificationlHandler;
import com.kalsym.usersservice.services.StoreAgentsHandler;
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
    
    @Autowired
    EmaiVerificationlHandler emaiVerificationlHandler;
    
    @Autowired
    StoreAgentsHandler storeAgentsHandler;
    
    @Value("${session.expiry:3600}")
    private int expiry;
    
    @GetMapping(path = {"/"}, name = "clients-get")
    @PreAuthorize("hasAnyAuthority('clients-get', 'all')")
    public ResponseEntity<HttpReponse> getClients(HttpServletRequest request,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String roleId,
            @RequestParam(required = false) String storeId,
            @RequestParam(required = false) Boolean locked,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());
        
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "", "");
        
        Client client = new Client();
        client.setUsername(username);
        client.setEmail(email);
        client.setRoleId(roleId);
        client.setLocked(locked);
        client.setStoreId(storeId);
        
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, client + "", "");
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withIgnorePaths("locked")
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Client> example = Example.of(client, matcher);
        
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "page: " + page + " pageSize: " + pageSize, "");
        Pageable pageable = PageRequest.of(page, pageSize);
        
        response.setSuccessStatus(HttpStatus.OK);
        response.setData(clientsRepository.findAll(example, pageable));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    @GetMapping(path = {"/{id}"}, name = "clients-get-by-id")
    @PreAuthorize("hasAnyAuthority('clients-get-by-id', 'all')")
    public ResponseEntity<HttpReponse> getClientById(HttpServletRequest request, @PathVariable String id) {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());
        
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "", "");
        
        Optional<Client> optClient = clientsRepository.findById(id);
        
        if (!optClient.isPresent()) {
            Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "client not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "client found", "");
        response.setSuccessStatus(HttpStatus.OK);
        response.setData(optClient.get());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    @DeleteMapping(path = {"/{id}"}, name = "clients-delete-by-id")
    @PreAuthorize("hasAnyAuthority('clients-delete-by-id', 'all')")
    public ResponseEntity<HttpReponse> deleteClientById(HttpServletRequest request, @PathVariable String id) {
        String logprefix = request.getRequestURI();
        
        HttpReponse response = new HttpReponse(request.getRequestURI());
        
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "", "");
        
        Optional<Client> optClient = clientsRepository.findById(id);
        
        if (!optClient.isPresent()) {
            Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "client not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "client found", "");
        
        if (optClient.get().getRoleId().equals("STORE_CSR_ORDER") || optClient.get().getRoleId().equals("STORE_CSR_COMPLAINT")) {
            storeAgentsHandler.deleteAgent(optClient.get().getLiveChatAgentId());
        }
        clientsRepository.delete(optClient.get());
        
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "client deleted", "");
        response.setSuccessStatus(HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    @PutMapping(path = {"/{id}"}, name = "clients-put-by-id")
    @PreAuthorize("hasAnyAuthority('clients-put-by-id', 'all')")
    public ResponseEntity<HttpReponse> putClientById(HttpServletRequest request, @PathVariable String id, @RequestBody Client body) {
        String logprefix = request.getRequestURI();
        
        HttpReponse response = new HttpReponse(request.getRequestURI());
        
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, body.toString(), "");
        
        Optional<Client> optClient = clientsRepository.findById(id);
        
        if (!optClient.isPresent()) {
            Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "client not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "client found", "");
        Client client = optClient.get();
        List<String> errors = new ArrayList<>();
        
        List<Client> clients = clientsRepository.findAll();
        
        for (Client existingClient : clients) {
            if (!client.equals(existingClient)) {
                if (existingClient.getUsername().equals(body.getUsername())) {
                    Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "username already exists", "");
                    response.setErrorStatus(HttpStatus.CONFLICT);
                    errors.add("username already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
                if (existingClient.getEmail().equals(body.getEmail())) {
                    Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "email already exists", "");
                    response.setErrorStatus(HttpStatus.CONFLICT);
                    errors.add("email already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
                if (null != body.getId()) {
                    if (existingClient.getEmail().equals(body.getEmail())) {
                        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "clientId already exists", "");
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
        
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "client updated for id: " + id, "");
        response.setSuccessStatus(HttpStatus.ACCEPTED);
        response.setData(clientsRepository.save(client));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
    
    @PostMapping(path = "/register", name = "clients-post")
    //@PreAuthorize("hasAnyAuthority('clients-post', 'all')")
    public ResponseEntity<HttpReponse> postClient(HttpServletRequest request,
            @Valid @RequestBody Client body) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());
        
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, body.toString(), "");
        
        List<String> errors = new ArrayList<>();
        if (null == body.getPassword() || body.getPassword().length() == 0) {
            Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "username already exists", "");
            response.setErrorStatus(HttpStatus.BAD_REQUEST);
            errors.add("password is required exists");
            response.setData(errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        List<Client> clients = clientsRepository.findAll();
        
        for (Client existingClient : clients) {
            if (existingClient.getUsername().equals(body.getUsername())) {
                Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "username already exists", "");
                response.setErrorStatus(HttpStatus.CONFLICT);
                errors.add("username already exists");
                response.setData(errors);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            if (existingClient.getEmail().equals(body.getEmail())) {
                Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "email already exists", "");
                response.setErrorStatus(HttpStatus.CONFLICT);
                errors.add("email already exists");
                response.setData(errors);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        }
        
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "client roleId: " + body.getRoleId(), "");
        
        String originalPassword = body.getPassword();
        body.setPassword(bcryptEncoder.encode(body.getPassword()));
        body.setLocked(false);
        body.setDeactivated(false);
        body = clientsRepository.save(body);
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "client created: " + body, "");
        if (body.getRoleId().equals("STORE_CSR_ORDER") || body.getRoleId().equals("STORE_CSR_COMPLAINT")) {
            LiveChatStoreAgent liveChatStoreAgent = new LiveChatStoreAgent();
            liveChatStoreAgent.setName(body.getName());
            liveChatStoreAgent.setEmail(body.getEmail());
            liveChatStoreAgent.setUsername(body.getUsername());
            liveChatStoreAgent.setPassword(originalPassword);
            List<String> roles = new ArrayList();
            roles.add("livechat-agent");
            liveChatStoreAgent.setRoles(roles);
            CustomFields customFields = new CustomFields();
            customFields.setStoreId(body.getStoreId());
            liveChatStoreAgent.setCustomFields(customFields);
            
            StoreAgentResponse lcr = storeAgentsHandler.createAgent(liveChatStoreAgent);
            
            if (lcr == null) {
                Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "agent could not be created");
                clientsRepository.delete(body);
                response.setErrorStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            } else {
                body.setLiveChatAgentId(lcr.get_id());
                clientsRepository.save(body);
            }
            Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "agent added");
            
        }
        
        emaiVerificationlHandler.sendVerificationEmail(body);
        body.setPassword(null);
        response.setSuccessStatus(HttpStatus.CREATED);
        response.setData(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping(path = {"/{id}/email-verification/{code}/verify"}, name = "clients-get-by-id")
    //@PreAuthorize("hasAnyAuthority('clients-get-by-id', 'all')")
    public ResponseEntity<HttpReponse> getCustomerVerify(HttpServletRequest request,
            @PathVariable String id,
            @PathVariable String code) {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());
        
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "", "");
        
        Optional<Client> optClient = clientsRepository.findById(id);
        
        if (!optClient.isPresent()) {
            Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "client not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "client found", "");
        
        boolean verified = emaiVerificationlHandler.verify(optClient.get(), code);
        
        if (!verified) {
            Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "cannot verify", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        response.setSuccessStatus(HttpStatus.OK);
        response.setData(optClient.get());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //authentication
    @PostMapping(path = "/authenticate", name = "clients-authenticate")
    public ResponseEntity authenticateClient(@Valid @RequestBody AuthenticationBody body,
            HttpServletRequest request) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "body: " + body);
        
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(body.getUsername(), body.getPassword())
            );
        } catch (BadCredentialsException e) {
            Logger.application.error(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "BadCredentialsException exception", e);
            response.setErrorStatus(HttpStatus.UNAUTHORIZED, "Bad Credentiails");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (AuthenticationException e) {
            Logger.application.error(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "AuthenticationException exception ", e);
            response.setErrorStatus(HttpStatus.UNAUTHORIZED, e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "client authenticated", "");
        
        Client client = clientsRepository.findByUsernameOrEmail(body.getUsername(), body.getUsername());
        
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
        
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "session: " + session, "");
        
        session = clientSessionsRepository.save(session);
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "session created with id: " + session.getId(), "");
        
        session.setUpdated(null);
        session.setStatus(null);
        session.setRemoteAddress(null);
        session.setId(null);
        
        Auth authReponse = new Auth();
        authReponse.setSession(session);
        authReponse.setAuthorities(authorities);
        authReponse.setRole(client.getRoleId());
        
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "generated token", "");
        
        response.setSuccessStatus(HttpStatus.ACCEPTED);
        response.setData(authReponse);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
    
    @PostMapping(path = "session/refresh", name = "clients-session-refresh")
    public ResponseEntity refreshSession(@Valid @RequestBody String refreshToken,
            HttpServletRequest request) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "refreshToken: " + refreshToken);
        
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, refreshToken, "");
        
        ClientSession session = clientSessionsRepository.findByRefreshToken(refreshToken);
        
        if (null == session) {
            Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "session not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        Optional<Client> optClient = clientsRepository.findById(session.getOwnerId());
        
        if (!optClient.isPresent()) {
            Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "client not found", "");
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
        
        ClientSession newSession = new ClientSession();
        newSession.setRemoteAddress(request.getRemoteAddr());
        newSession.setOwnerId(optClient.get().getId());
        newSession.setUsername(optClient.get().getUsername());
        newSession.setCreated(DateTimeUtil.currentTimestamp());
        newSession.setUpdated(DateTimeUtil.currentTimestamp());
        newSession.setExpiry(DateTimeUtil.expiryTimestamp(expiry));
        newSession.setStatus("ACTIVE");
        newSession.generateTokens();
        
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "session: " + newSession, "");
        
        newSession = clientSessionsRepository.save(newSession);
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "session created with id: " + newSession.getId(), "");
        
        newSession.setOwnerId(null);
        newSession.setUpdated(null);
        newSession.setStatus(null);
        newSession.setRemoteAddress(null);
        newSession.setUsername(null);
        newSession.setId(null);
        
        Auth authReponse = new Auth();
        authReponse.setSession(newSession);
        authReponse.setAuthorities(authorities);
        authReponse.setRole(optClient.get().getRoleId());
        
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "generated token", "");
        
        response.setSuccessStatus(HttpStatus.ACCEPTED);
        response.setData(authReponse);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
    
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity handleExceptionBadRequestException(HttpServletRequest request, MethodArgumentNotValidException e) {
        String logprefix = request.getRequestURI();
        
        Logger.application.warn(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "validation failed", "");
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());
        HttpReponse response = new HttpReponse(request.getRequestURI());
        response.setErrorStatus(HttpStatus.BAD_REQUEST);
        response.setData(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
