package com.kalsym.userservice.controllers;

import com.kalsym.userservice.models.Store;
import com.kalsym.userservice.models.storeagent.StoreAgentResponse;
import com.kalsym.userservice.models.storeagent.CustomFields;
import com.kalsym.userservice.models.storeagent.LiveChatStoreAgent;
import com.kalsym.userservice.UserServiceApplication;
import com.kalsym.userservice.models.Auth;
import com.kalsym.userservice.models.HttpReponse;
import com.kalsym.userservice.models.ChangePassword;
import com.kalsym.userservice.models.daos.RoleAuthority;
import com.kalsym.userservice.models.daos.ClientSession;
import com.kalsym.userservice.models.daos.Client;
import com.kalsym.userservice.models.requestbodies.AuthenticationBody;
import com.kalsym.userservice.models.requestbodies.TempTokenRequest;
import com.kalsym.userservice.models.requestbodies.ValidateOauthRequest;
import com.kalsym.userservice.models.storeagent.LiveChatResponse;
import com.kalsym.userservice.repositories.RoleAuthoritiesRepository;
import com.kalsym.userservice.repositories.ClientSessionsRepository;
import com.kalsym.userservice.repositories.ClientsRepository;
import com.kalsym.userservice.repositories.StoreRepository;
import com.kalsym.userservice.services.EmaiVerificationlHandler;
import com.kalsym.userservice.services.GoogleAuthService;
import com.kalsym.userservice.services.FacebookAuthService;
import com.kalsym.userservice.services.StoreAgentsHandler;
import com.kalsym.userservice.utils.DateTimeUtil;
import com.kalsym.userservice.utils.Logger;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

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

    @Autowired
    StoreRepository storeRepository;

    @Value("${session.expiry:3600}")
    private int expiry;

    @Value("${email.verification.enabled:false}")
    private Boolean emailVerificationEnabled;
    
    @Autowired
    GoogleAuthService googleAuthService;
    
    @Autowired
    FacebookAuthService facebookAuthService;
    
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

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        Client client = new Client();
        client.setUsername(username);
        client.setEmail(email);
        client.setRoleId(roleId);
        client.setLocked(locked);
        client.setStoreId(storeId);

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, client + "", "");
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withIgnorePaths("locked")
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Client> example = Example.of(client, matcher);

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "page: " + page + " pageSize: " + pageSize, "");
        Pageable pageable = PageRequest.of(page, pageSize);

        response.setStatus(HttpStatus.OK);
        response.setData(clientsRepository.findAll(example, pageable));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(path = {"/{id}"}, name = "clients-get-by-id")
    @PreAuthorize("hasAnyAuthority('clients-get-by-id', 'all')")
    public ResponseEntity<HttpReponse> getClientById(HttpServletRequest request, @PathVariable String id) {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        Optional<Client> optClient = clientsRepository.findById(id);

        if (!optClient.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client found", "");
        response.setStatus(HttpStatus.OK);
        response.setData(optClient.get());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping(path = {"/{id}"}, name = "clients-delete-by-id")
    @PreAuthorize("hasAnyAuthority('clients-delete-by-id', 'all')")
    public ResponseEntity<HttpReponse> deleteClientById(HttpServletRequest request, @PathVariable String id) {
        String logprefix = request.getRequestURI();

        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        Optional<Client> optClient = clientsRepository.findById(id);

        if (!optClient.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client found", "");
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client role" + optClient.get().getRoleId(), "");
        if (optClient.get().getRoleId().equals("STORE_CSR_ORDER") || optClient.get().getRoleId().equals("STORE_CSR_COMPLAINT") || optClient.get().getRoleId().equals("STORE_CSR_ADMIN")) {
            storeAgentsHandler.deleteAgent(optClient.get().getLiveChatAgentId());
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client deleted from rocket chat", "");

        }
        clientsRepository.delete(optClient.get());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client deleted", "");
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(path = {"/{id}"}, name = "clients-put-by-id")
    @PreAuthorize("hasAnyAuthority('clients-put-by-id', 'all')")
    public ResponseEntity<HttpReponse> putClientById(HttpServletRequest request, @PathVariable String id, @RequestBody Client body) {
        String logprefix = request.getRequestURI();

        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, body.toString(), "");

        Optional<Client> optClient = clientsRepository.findById(id);

        if (!optClient.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client found", "");
        Client client = optClient.get();
        List<String> errors = new ArrayList<>();

        List<Client> clients = clientsRepository.findAll();

        for (Client existingClient : clients) {
            if (!client.equals(existingClient)) {
                if (existingClient.getUsername().equals(body.getUsername())) {
                    Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "username already exists", "");
                    response.setStatus(HttpStatus.CONFLICT);
                    errors.add("username already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
                if (existingClient.getEmail().equals(body.getEmail())) {
                    Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "email already exists", "");
                    response.setStatus(HttpStatus.CONFLICT);
                    errors.add("email already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
                if (null != body.getId()) {
                    if (existingClient.getEmail().equals(body.getEmail())) {
                        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "clientId already exists", "");
                        response.setStatus(HttpStatus.CONFLICT);
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

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client updated for id: " + id, "");
        response.setStatus(HttpStatus.ACCEPTED);
        response.setData(clientsRepository.save(client));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(path = "/register", name = "clients-post")
    //@PreAuthorize("hasAnyAuthority('clients-post', 'all')")
    public ResponseEntity<HttpReponse> postClient(HttpServletRequest request,
            @Valid @RequestBody Client body) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        try {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, body.toString(), "");

            List<String> errors = new ArrayList<>();
            if (null == body.getPassword() || body.getPassword().length() == 0) {
                Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "password not provided", "");
                response.setStatus(HttpStatus.BAD_REQUEST);
                errors.add("password is required");
                response.setData(errors);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            List<Client> clients = clientsRepository.findAll();

            for (Client existingClient : clients) {
                if (existingClient.getUsername().equalsIgnoreCase(body.getUsername())) {
                    Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "username already exists", "");
                    response.setStatus(HttpStatus.CONFLICT);
                    errors.add("username already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
                if (existingClient.getEmail().equals(body.getEmail())) {
                    Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "email already exists", "");
                    response.setStatus(HttpStatus.CONFLICT);
                    errors.add("email already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
            }

            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client roleId: " + body.getRoleId(), "");

            String originalPassword = body.getPassword();
            body.setPassword(bcryptEncoder.encode(body.getPassword()));
            body.setLocked(false);
            body.setDeactivated(false);
            body = clientsRepository.save(body);
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client created: " + body, "");
            if (body.getRoleId().equals("STORE_CSR_ORDER")
                    || body.getRoleId().equals("STORE_CSR_COMPLAINT")
                    || body.getRoleId().equals("STORE_CSR_ADMIN")) {
                LiveChatStoreAgent liveChatStoreAgent = new LiveChatStoreAgent();
                liveChatStoreAgent.setName(body.getName());
                liveChatStoreAgent.setEmail(body.getEmail());
                liveChatStoreAgent.setUsername(body.getUsername());
                liveChatStoreAgent.setPassword(originalPassword);
                List<String> roles = new ArrayList();
                roles.add("livechat-agent");

                Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "storeId: " + body.getStoreId());

                try {
                    if (null != body.getStoreId()) {
                        Optional<Store> optStore = storeRepository.findById(body.getStoreId());

                        if (optStore.isPresent()) {
                            Store store = optStore.get();
                            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "vertical of store: " + store.getVerticalCode());
                            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, store.getVerticalCode().toLowerCase() + " contains fnb :" + store.getVerticalCode().toLowerCase().contains("fnb"));

                            if (store.getVerticalCode().toLowerCase().contains("fnb")) {
                                roles.add("fnb");
                                Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "added role fnb");

                            }
                        }
                    }
                } catch (Exception e) {
                    Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "error checking vertical");

                }

                liveChatStoreAgent.setRoles(roles);
                CustomFields customFields = new CustomFields();
                customFields.setStoreId(body.getStoreId());
                liveChatStoreAgent.setCustomFields(customFields);
                liveChatStoreAgent.setJoinDefaultChannels(false);

                LiveChatResponse lcr = null;
                String liveChatId = "";
                try {
                    lcr = storeAgentsHandler.createAgent(liveChatStoreAgent);

                    if (lcr.success == true) {
                        liveChatId = lcr.getUser()._id;
                    } else {
                        Logger.application.error(Logger.pattern, UserServiceApplication.VERSION, logprefix, "agent could not be created because" + lcr.getError());
                        clientsRepository.delete(body);
                        response.setStatus(HttpStatus.CONFLICT);
                        response.setError(lcr.getError());
                        response.setMessage(lcr.getError());
                        return ResponseEntity.status(response.getStatus()).body(response);
                    }
                } catch (Exception e) {
                    Logger.application.error(Logger.pattern, UserServiceApplication.VERSION, logprefix, "agent could not be created because", e);

                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                    response.setError(e.getMessage());
                    response.setMessage(e.getMessage());
                    return ResponseEntity.status(response.getStatus()).body(response);
                }

                try {

                    if (body.getRoleId().equals("STORE_CSR_ORDER")) {
                        storeAgentsHandler.inviteOrderAgentToGroup(liveChatId, body.getStoreId());
                    } else if (body.getRoleId().equals("STORE_CSR_COMPLAINT")) {
                        storeAgentsHandler.inviteComplaintCsrAgentToGroup(liveChatId, body.getStoreId());
                    } else if (body.getRoleId().equals("STORE_CSR_ADMIN")) {
                        storeAgentsHandler.inviteComplaintCsrAgentToGroup(liveChatId, body.getStoreId());
                        storeAgentsHandler.inviteOrderAgentToGroup(liveChatId, body.getStoreId());

                    }
                } catch (Exception e) {
                    Logger.application.error(Logger.pattern, UserServiceApplication.VERSION, logprefix, "agent could not be created", e);

                    clientsRepository.delete(body);
                    Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "deleted client", "");

                    storeAgentsHandler.deleteAgent(liveChatId);
                    Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "deleted agent", "");

                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                    response.setError(e.toString());
                    return ResponseEntity.status(response.getStatus()).body(response);
                }
                body.setLiveChatAgentId(liveChatId);
                clientsRepository.save(body);

                Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "agent added");

            }

            if (emailVerificationEnabled) {
                if (!emaiVerificationlHandler.sendVerificationEmail(body)) {
                    Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "could not verification email", "");
                    clientsRepository.delete(body);
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                    response.setError("Error sending verification email");
                    return ResponseEntity.status(response.getStatus()).body(response);
                }
            }

            body.setPassword(null);
            response.setStatus(HttpStatus.CREATED);
            response.setData(body);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (Exception e) {
            Logger.application.error(Logger.pattern, UserServiceApplication.VERSION, logprefix, "error creating client", "", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setError(e.toString());
            return ResponseEntity.status(response.getStatus()).body(response);
        }

    }

    @GetMapping(path = {"/{id}/email-verification/{code}/verify"}, name = "clients-email-verification-by-id")
    //@PreAuthorize("hasAnyAuthority('clients-get-by-id', 'all')")
    public ResponseEntity<HttpReponse> getClientVerify(HttpServletRequest request,
            @PathVariable String id,
            @PathVariable String code) {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        Optional<Client> optClient = clientsRepository.findById(id);

        if (!optClient.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Client existingClient = optClient.get();
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client found", "");

        boolean verified = emaiVerificationlHandler.verifyEmail(existingClient, code);

        if (!verified) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "cannot verify", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        response.setStatus(HttpStatus.OK);
        response.setData(existingClient);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(path = {"/{id}/password/{code}/reset"}, name = "clients-get-by-id")
    //@PreAuthorize("hasAnyAuthority('clients-get-by-id', 'all')")
    public ResponseEntity<HttpReponse> putClientPasswordReset(HttpServletRequest request,
            @PathVariable String id,
            @PathVariable String code,
            @RequestBody Client body) {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        Optional<Client> optClient = clientsRepository.findById(id);

        if (!optClient.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Client existingClient = optClient.get();

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client found", "");

        boolean verified = emaiVerificationlHandler.verifyPasswordReset(existingClient, code);

        if (!verified) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "cannot verify", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("Code not valid");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        existingClient.setPassword(bcryptEncoder.encode(body.getPassword()));
        existingClient = clientsRepository.save(existingClient);
        response.setStatus(HttpStatus.ACCEPTED);
        response.setData(existingClient);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(path = {"/{email}/password_reset"}, name = "clients-password_reset-post-by-id")
    public ResponseEntity<HttpReponse> postClientPasswordReset(HttpServletRequest request,
            @PathVariable String email) {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        Client client = clientsRepository.findByUsernameOrEmail(email, email);

        if (client == null) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client found", "");

        try {
            emaiVerificationlHandler.sendPasswordReset(client);
        } catch (Exception e) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "error sending email ", "", e);

            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setError(e.toString());
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        response.setStatus(HttpStatus.OK);
        response.setData(client);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    //authentication
    @PostMapping(path = "/authenticate", name = "clients-authenticate")
    public ResponseEntity authenticateClient(@Valid @RequestBody AuthenticationBody body,
            HttpServletRequest request) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "body: " + body);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(body.getUsername(), body.getPassword())
            );
        } catch (BadCredentialsException e) {
            Logger.application.error(Logger.pattern, UserServiceApplication.VERSION, logprefix, "BadCredentialsException exception", e);
            response.setStatus(HttpStatus.UNAUTHORIZED, "Bad Credentiails");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (AuthenticationException e) {
            Logger.application.error(Logger.pattern, UserServiceApplication.VERSION, logprefix, "AuthenticationException exception ", e);
            response.setStatus(HttpStatus.UNAUTHORIZED, e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client authenticated", "");

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

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "session: " + session, "");

        session = clientSessionsRepository.save(session);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "session created with id: " + session.getId(), "");

        session.setUpdated(null);
        session.setStatus(null);
        session.setRemoteAddress(null);
        session.setId(null);

        Auth authReponse = new Auth();
        authReponse.setSession(session);
        authReponse.setAuthorities(authorities);
        authReponse.setRole(client.getRoleId());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "generated token", "");

        response.setStatus(HttpStatus.ACCEPTED);
        response.setData(authReponse);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
    
    //authentication
    @PostMapping(path = "/loginoauth", name = "clients-authenticate")
    public ResponseEntity loginOauth(@Valid @RequestBody ValidateOauthRequest body,
            HttpServletRequest request) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "body: " + body);
        
        String userEmail = null;
        
        if (body.getLoginType().equalsIgnoreCase("GOOGLE")) {
            //validate token with google
            Optional<GoogleAuthService.GoogleUserInfo> googleResult = googleAuthService.getUserInfo(body.getToken());
            if (googleResult.isPresent()) {
                //authenticated
                if (googleResult.get().email.equals(body.getEmail())) {
                    //check if email is same
                    userEmail = body.getEmail();
                }
            }
        } else if (body.getLoginType().equalsIgnoreCase("FACEBOOK")) {
            //validate token with facebook
            Optional<FacebookAuthService.FacebookUserInfo> fbResult = facebookAuthService.getUserInfo(body.getToken());
            if (fbResult.isPresent()) {
                //authenticated
                if (fbResult.get().userId.equals(body.getUserId())) {
                    //check if email is same
                    userEmail = body.getEmail();
                    Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "User Id is valid");
                }
            }
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "userEmail:"+userEmail+" FB response userId:"+fbResult.get().userId+" UserId from request:"+body.getUserId());
        } else if (body.getLoginType().equalsIgnoreCase("APPLE")) {
            //validate token with apple
        }            
        
        if (userEmail==null) {
            Logger.application.error(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Authentication failed");
            response.setStatus(HttpStatus.UNAUTHORIZED, "Fail to validate token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client authenticated", "");

        Client client = clientsRepository.findByUsernameOrEmail(userEmail, userEmail);
        
        if (client == null) {
            //create new account
            client = new Client();
            client.setEmail(userEmail);
            client.setRoleId(logprefix);
            client.setUsername(userEmail);
            client.setLocked(false);
            client.setDeactivated(false);
            client.setRoleId("STORE_OWNER");
            client.setName(body.getName());
            client.setCountryId(body.getCountry());
            client = clientsRepository.save(client);
        }
        
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

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "session: " + session, "");

        session = clientSessionsRepository.save(session);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "session created with id: " + session.getId(), "");

        session.setUpdated(null);
        session.setStatus(null);
        session.setRemoteAddress(null);
        session.setId(null);

        Auth authReponse = new Auth();
        authReponse.setSession(session);
        authReponse.setAuthorities(authorities);
        authReponse.setRole(client.getRoleId());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "generated token", "");

        response.setStatus(HttpStatus.ACCEPTED);
        response.setData(authReponse);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(path = "session/refresh", name = "clients-session-refresh")
    public ResponseEntity refreshSession(@Valid @RequestBody String refreshToken,
            HttpServletRequest request) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "refreshToken: [" + refreshToken + "]");

        ClientSession session = clientSessionsRepository.findByRefreshToken(refreshToken);

        if (null == session) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "session not found in clientSession", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Optional<Client> optClient = clientsRepository.findById(session.getOwnerId());

        if (!optClient.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
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

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "session: " + newSession, "");

        newSession = clientSessionsRepository.save(newSession);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "session created with id: " + newSession.getId(), "");

        newSession.setUpdated(null);
        newSession.setStatus(null);
        newSession.setRemoteAddress(null);
        newSession.setUsername(null);
        newSession.setId(null);

        Auth authReponse = new Auth();
        authReponse.setSession(newSession);
        authReponse.setAuthorities(authorities);
        authReponse.setRole(optClient.get().getRoleId());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "generated token", "");

        response.setStatus(HttpStatus.ACCEPTED);
        response.setData(authReponse);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
    
    //authentication
    @PostMapping(path = "/generateTempToken", name = "clients-authenticate")
    public ResponseEntity generateTempToken(@Valid @RequestBody TempTokenRequest body,
            HttpServletRequest request) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "body: " + body);

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client authenticated", "");

        Client client = clientsRepository.findByUsernameAndPasswordAndId(body.getUsername(), body.getPassword(), body.getClientId());
        
        if (client==null) {
            Logger.application.error(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Authentication Failed");
            response.setStatus(HttpStatus.UNAUTHORIZED, "Authentication Failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        List<RoleAuthority> roleAuthories = roleAuthoritiesRepository.findByRoleId(client.getRoleId());
        ArrayList<String> authorities = new ArrayList<>();
        if (null != roleAuthories) {

            for (RoleAuthority roleAuthority : roleAuthories) {
                authorities.add(roleAuthority.getAuthorityId());
            }
        }
        
        int tempTokenExpiryInSecond = 300;//5 minutes
        ClientSession session = new ClientSession();
        session.setRemoteAddress(request.getRemoteAddr());
        session.setOwnerId(client.getId());
        session.setUsername(client.getUsername());
        session.setCreated(DateTimeUtil.currentTimestamp());
        session.setUpdated(DateTimeUtil.currentTimestamp());
        session.setExpiry(DateTimeUtil.expiryTimestamp(tempTokenExpiryInSecond));
        session.setStatus("ACTIVE");
        session.generateTokens();

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "session: " + session, "");

        session = clientSessionsRepository.save(session);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "session created with id: " + session.getId(), "");

        session.setUpdated(null);
        session.setStatus(null);
        session.setRemoteAddress(null);
        session.setId(null);

        Auth authReponse = new Auth();
        authReponse.setSession(session);
        authReponse.setAuthorities(authorities);
        authReponse.setRole(client.getRoleId());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "temp token generated", "");

        response.setStatus(HttpStatus.ACCEPTED);
        response.setData(authReponse);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
    
    
    @PutMapping(path = {"/{id}/changepassword"}, name = "clients-put-by-id")
    @PreAuthorize("hasAnyAuthority('clients-put-by-id', 'all')")
    public ResponseEntity<HttpReponse> changePasswordClientById(HttpServletRequest request, @PathVariable String id, @RequestBody ChangePassword body) {
        String logprefix = request.getRequestURI();

        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, body.toString(), "");

        Optional<Client> optClient = clientsRepository.findById(id);

        if (!optClient.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client found", "");
        Client client = optClient.get();
        List<String> errors = new ArrayList<>();
        
        if (!body.getNewPassword().equals(body.getConfirmNewPassword())) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "new password is not same", "");
            response.setStatus(HttpStatus.CONFLICT, "Confirm new password not same");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
        //verify current password
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(client.getUsername(), body.getCurrentPassword())
            );
        } catch (BadCredentialsException e) {
            Logger.application.error(Logger.pattern, UserServiceApplication.VERSION, logprefix, "BadCredentialsException exception", e);
            response.setStatus(HttpStatus.FORBIDDEN, "Bad Credentiails");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (AuthenticationException e) {
            Logger.application.error(Logger.pattern, UserServiceApplication.VERSION, logprefix, "AuthenticationException exception ", e);
            response.setStatus(HttpStatus.FORBIDDEN, e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }        
        
        Client clientBody = new Client();
        clientBody.setId(id);
        if (null != body.getNewPassword() && body.getNewPassword().length() > 0) {
            clientBody.setPassword(bcryptEncoder.encode(body.getNewPassword()));
        } else {
            clientBody.setPassword(null);
        }

        client.update(clientBody);
        client.setUpdated(DateTimeUtil.currentTimestamp());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client updated for id: " + id, "");
        response.setStatus(HttpStatus.ACCEPTED);
        response.setData(clientsRepository.save(client));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
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
