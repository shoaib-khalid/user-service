package com.kalsym.usersservice.controllers;

import com.kalsym.usersservice.models.AuthenticationReponse;
import com.kalsym.usersservice.models.HttpReponse;
import com.kalsym.usersservice.models.UserProfile;
import com.kalsym.usersservice.models.daos.RoleAuthority;
import com.kalsym.usersservice.models.daos.Customer;
import com.kalsym.usersservice.models.daos.Session;
import com.kalsym.usersservice.models.daos.User;
import com.kalsym.usersservice.models.requestbodies.UserAuthenticationBody;
import com.kalsym.usersservice.repositories.RoleAuthoritiesRepository;
import com.kalsym.usersservice.repositories.SessionsRepository;
import com.kalsym.usersservice.repositories.UsersRepository;
import com.kalsym.usersservice.utils.DateTimeUtil;
import com.kalsym.usersservice.utils.LogUtil;
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
import com.kalsym.usersservice.repositories.CustomersRepository;
import com.kalsym.usersservice.services.MySQLUserDetailsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

/**
 *
 * @author Sarosh
 */
@RestController()
@RequestMapping("/users")
public class UsersController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    RoleAuthoritiesRepository roleAuthoritiesRepository;

    @Autowired
    private CustomersRepository customerRepository;

    @Autowired
    SessionsRepository sessionsRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;
    
    @Autowired
    private MySQLUserDetailsService jwtUserDetailsService;
    
    @Value("${session.expiry:3600}")
    private int expiry;

    @GetMapping(path = {"/"}, name = "auth-service_users-get")
    @PreAuthorize("hasAnyAuthority('auth-service_users-get', 'all')")
    public ResponseEntity<HttpReponse> getUsers(HttpServletRequest request,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String roleId,
            @RequestParam(required = false) Boolean locked,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        LogUtil.info(logprefix, location, "", "");

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setRoleId(roleId);
        user.setLocked(locked);

        LogUtil.info(logprefix, location, user+"", "");
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withIgnorePaths("locked")
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<User> example = Example.of(user, matcher);

        LogUtil.info(logprefix, location, "page: " + page + " pageSize: " + pageSize, "");
        Pageable pageable = PageRequest.of(page, pageSize);

        response.setSuccessStatus(HttpStatus.OK);
        response.setData(usersRepository.findAll(example, pageable));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = {"/{id}"}, name = "auth-service_users-get-by-id")
    @PreAuthorize("hasAnyAuthority('auth-service_users-get-by-id', 'all')")
    public ResponseEntity<HttpReponse> getUserById(HttpServletRequest request, @PathVariable String id) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        LogUtil.info(logprefix, location, "", "");

        Optional<User> optUser = usersRepository.findById(id);

        if (!optUser.isPresent()) {
            LogUtil.info(logprefix, location, "user not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        LogUtil.info(logprefix, location, "user found", "");
        response.setSuccessStatus(HttpStatus.OK);
        response.setData(optUser.get());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    @GetMapping(path = {"/profile"}, name = "auth-service_users-get-profile")
    @PreAuthorize("hasAnyAuthority('auth-service_users-get-profile', 'all')")
    public ResponseEntity<HttpReponse> getUserProfile(HttpServletRequest request) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        LogUtil.info(logprefix, location, "", "");
        
        final String requestTokenHeader = request.getHeader("Authorization");
        
        //LogUtil.info(logprefix, location, "requestTokenHeader: " + requestTokenHeader, "");
        String sessionId = null;

        // Token is in the form "Bearer token". Remove Bearer word and get only the Token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            sessionId = requestTokenHeader.substring(7);
        } else {
            LogUtil.warn(logprefix, location, "token does not begin with Bearer String", "");
        }

        if (sessionId != null) {
            //LogUtil.info(logprefix, location, "sessionId: " + sessionId, "");
            Optional<Session> optSession = sessionsRepository.findById(sessionId);
            if (optSession.isPresent()) {
                //get user details from session
                LogUtil.info(logprefix, location, "sessionId valid", "");
                Session session = optSession.get();                
                Optional<User> optUser = usersRepository.findById(session.getUserId());

                if (!optUser.isPresent()) {                    
                    LogUtil.info(logprefix, location, "user not found", "");
                    response.setErrorStatus(HttpStatus.NOT_FOUND);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                } else {
                    List<RoleAuthority> auths = roleAuthoritiesRepository.findByRoleId(optUser.get().getRoleId());                
                    LogUtil.info(logprefix, location, "user found", "");
                    response.setSuccessStatus(HttpStatus.OK);
                    UserProfile userProfile = new UserProfile(optUser.get(),auths);
                    response.setData(userProfile);
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }
            } else {
                LogUtil.info(logprefix, location, "user not found", "");
                response.setErrorStatus(HttpStatus.NOT_FOUND);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } else {
            LogUtil.info(logprefix, location, "sessionId not valid", "");
            response.setErrorStatus(HttpStatus.FORBIDDEN);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }
    

    @DeleteMapping(path = {"/{id}"}, name = "auth-service_users-delete-by-id")
    @PreAuthorize("hasAnyAuthority('auth-service_users-delete-by-id', 'all')")
    public ResponseEntity<HttpReponse> deleteUserById(HttpServletRequest request, @PathVariable String id) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        LogUtil.info(logprefix, location, "", "");

        Optional<User> optUser = usersRepository.findById(id);

        if (!optUser.isPresent()) {
            LogUtil.info(logprefix, location, "user not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        LogUtil.info(logprefix, location, "user found", "");
        usersRepository.delete(optUser.get());

        LogUtil.info(logprefix, location, "user deleted", "");
        response.setSuccessStatus(HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping(path = {"/{id}"}, name = "auth-service_users-put-by-id")
    @PreAuthorize("hasAnyAuthority('auth-service_users-put-by-id', 'all')")
    public ResponseEntity<HttpReponse> putUserById(HttpServletRequest request, @PathVariable String id, @RequestBody User body) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        LogUtil.info(logprefix, location, "", "");
        LogUtil.info(logprefix, location, body.toString(), "");

        Optional<User> optUser = usersRepository.findById(id);

        if (!optUser.isPresent()) {
            LogUtil.info(logprefix, location, "user not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        LogUtil.info(logprefix, location, "user found", "");
        User user = optUser.get();
        List<String> errors = new ArrayList<>();

        List<User> users = usersRepository.findAll();

        for (User existingUser : users) {
            if (!user.equals(existingUser)) {
                if (existingUser.getUsername().equals(body.getUsername())) {
                    LogUtil.info(logprefix, location, "username already exists", "");
                    response.setErrorStatus(HttpStatus.CONFLICT);
                    errors.add("username already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
                if (existingUser.getEmail().equals(body.getEmail())) {
                    LogUtil.info(logprefix, location, "email already exists", "");
                    response.setErrorStatus(HttpStatus.CONFLICT);
                    errors.add("email already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
                if (null != body.getId()) {
                    if (existingUser.getEmail().equals(body.getEmail())) {
                        LogUtil.info(logprefix, location, "userId already exists", "");
                        response.setErrorStatus(HttpStatus.CONFLICT);
                        errors.add("userId already exists");
                        response.setData(errors);
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                    }
                }
            }

        }

        if (null != body.getPassword() && body.getPassword().length()>0) {
            body.setPassword(bcryptEncoder.encode(body.getPassword()));
        }else{
            body.setPassword(null);
        }

        user.updateUser(body);
        user.setUpdated(DateTimeUtil.currentTimestamp());

        LogUtil.info(logprefix, location, "user updated for id: " + id, "");
        response.setSuccessStatus(HttpStatus.ACCEPTED);
        response.setData(usersRepository.save(user));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(name = "auth-service_users-post")
    @PreAuthorize("hasAnyAuthority('auth-service_users-post', 'all')")
    public ResponseEntity<HttpReponse> postUser(HttpServletRequest request, @Valid @RequestBody User body) throws Exception {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        LogUtil.info(logprefix, location, "", "");
        LogUtil.info(logprefix, location, body.toString(), "");
        
        List<String> errors = new ArrayList<>();
        if (null==body.getPassword() || body.getPassword().length()==0) {
                LogUtil.info(logprefix, location, "username already exists", "");
                response.setErrorStatus(HttpStatus.BAD_REQUEST);
                errors.add("password is required exists");
                response.setData(errors);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        List<User> users = usersRepository.findAll();
        

        for (User existingUser : users) {
            if (existingUser.getUsername().equals(body.getUsername())) {
                LogUtil.info(logprefix, location, "username already exists", "");
                response.setErrorStatus(HttpStatus.CONFLICT);
                errors.add("username already exists");
                response.setData(errors);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            if (existingUser.getEmail().equals(body.getEmail())) {
                LogUtil.info(logprefix, location, "email already exists", "");
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
        body = usersRepository.save(body);
        body.setPassword(null);
        LogUtil.info(logprefix, location, "user created with id: " + body.getId(), "");
        response.setSuccessStatus(HttpStatus.CREATED);
        response.setData(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

 
    @GetMapping(path = {"/{userId}/customer"}, name = "auth-service_users-get-customer-by-userId")
    @PreAuthorize("hasAnyAuthority('auth-service_users-get-authorities-by-userId', 'all')")
    public HttpReponse getCustomerByUserId(HttpServletRequest request, @PathVariable String userId) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        LogUtil.info(logprefix, location, "", "");

        Optional<Customer> optSeller = customerRepository.findById(userId);

        if (!optSeller.isPresent()) {
            LogUtil.info(logprefix, location, "customer not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return response;
        }

        LogUtil.info(logprefix, location, "customer found", "");

        response.setSuccessStatus(HttpStatus.OK);
        response.setData(optSeller.get());
        return response;
    }

    @PutMapping(path = {"/{userId}/customer"}, name = "auth-service_users-put-customer-by-userId")
    @PreAuthorize("hasAnyAuthority('auth-service_users-put-customer-by-userId', 'all')")
    public ResponseEntity<HttpReponse> putCustomerByUserId(HttpServletRequest request, @PathVariable String userId,
            @RequestBody Customer body) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        LogUtil.info(logprefix, location, "", "");
        LogUtil.info(logprefix, location, body.toString(), "");

        Optional<Customer> optSeller = customerRepository.findById(userId);

        if (!optSeller.isPresent()) {
            LogUtil.info(logprefix, location, "customer not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        LogUtil.info(logprefix, location, "customer found", "");
        Customer seller = optSeller.get();
        List<String> errors = new ArrayList<>();

        List<Customer> sellers = customerRepository.findAll();

        for (Customer existingSeller : sellers) {
            if (!seller.equals(existingSeller)) {
                if (existingSeller.getMobileNumber().equals(body.getMobileNumber())) {
                    LogUtil.info(logprefix, location, "mobilenumber already exists", "");
                    response.setErrorStatus(HttpStatus.CONFLICT);
                    errors.add("mobileNumber already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
                if (existingSeller.getUserId().equals(body.getUserId())) {
                    LogUtil.info(logprefix, location, "userId already exists", "");
                    response.setErrorStatus(HttpStatus.CONFLICT);
                    errors.add("userId already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
            }
        }

        seller.updateCustomer(body);

        LogUtil.info(logprefix, location, "seller updated created for userId: " + userId, "");
        response.setSuccessStatus(HttpStatus.ACCEPTED);
        response.setData(customerRepository.save(seller));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(path = {"/{userId}/customer"}, name = "auth-service_users-post-customer-by-userId")
    @PreAuthorize("hasAnyAuthority('auth-service_users-post-customer-by-userId', 'all')")
    public ResponseEntity postSellerByUserId(HttpServletRequest request, @PathVariable String userId,
            @Valid @RequestBody Customer body) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        LogUtil.info(logprefix, location, "", "");
        LogUtil.info(logprefix, location, body.toString(), "");

        Optional<User> optUser = usersRepository.findById(userId);

        if (!optUser.isPresent()) {
            LogUtil.info(logprefix, location, "user not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND, "user not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        LogUtil.info(logprefix, location, "user found", "");

        List<String> errors = new ArrayList<>();
        List<Customer> sellers = customerRepository.findAll();

        for (Customer existingSeller : sellers) {
            if (existingSeller.getMobileNumber().equals(body.getMobileNumber())) {
                LogUtil.info(logprefix, location, "mobilenumber already exists", "");
                response.setErrorStatus(HttpStatus.CONFLICT);
                errors.add("mobileNumber already exists");
                response.setData(errors);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            if (existingSeller.getUserId().equals(body.getUserId())) {
                LogUtil.info(logprefix, location, "userId already exists", "");
                response.setErrorStatus(HttpStatus.CONFLICT);
                errors.add("userId already exists");
                response.setData(errors);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        }

        body.setUserId(userId);

        LogUtil.info(logprefix, location, "customer created for userId: " + userId, "");
        response.setSuccessStatus(HttpStatus.CREATED);
        response.setData(customerRepository.save(body));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //authentication
    @PostMapping(path = "/authenticate", name = "auth-service_users-authenticate")
    public ResponseEntity authenticateUser(@Valid @RequestBody UserAuthenticationBody body,
            HttpServletRequest request) throws Exception {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        LogUtil.info(logprefix, location, "", "");

        try {
            String pwd = bcryptEncoder.encode(body.getPassword());
            LogUtil.info(logprefix, location, "pwd:["+pwd+"]", "");
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(body.getUsername(), body.getPassword())
            );
        } catch (BadCredentialsException e) {
            LogUtil.warn(logprefix, location, "error validating user", "");
            response.setErrorStatus(HttpStatus.UNAUTHORIZED, "Bad Craedentiails");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }catch(Exception e){
            LogUtil.warn(logprefix, location, "error validating user", "");
            response.setErrorStatus(HttpStatus.UNAUTHORIZED, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        LogUtil.info(logprefix, location, "user authenticated", "");

        User user = usersRepository.findByUsername(body.getUsername());

        List<RoleAuthority> roleAuthories = roleAuthoritiesRepository.findByRoleId(user.getRoleId());
        ArrayList<String> authorities = new ArrayList<>();
        if (null != roleAuthories) {

            for (RoleAuthority roleAuthority : roleAuthories) {
                authorities.add(roleAuthority.getAuthorityId());
            }
        }

        Session session = new Session();
        session.setRemoteAddress(request.getRemoteAddr());
        session.setUserId(user.getId());
        session.setCreated(DateTimeUtil.currentTimestamp());
        session.setUpdated(DateTimeUtil.currentTimestamp());
        session.setExpiry(DateTimeUtil.expiryTimestamp(expiry));
        session.setStatus("ACTIVE");
        session = sessionsRepository.save(session);
        LogUtil.info(logprefix, location, "session created with id: " + session.getId(), "");

        session.setUserId(null);
        session.setUpdated(null);
        session.setStatus(null);
        session.setRemoteAddress(null);

        AuthenticationReponse authReponse = new AuthenticationReponse();
        authReponse.setSession(session);
        authReponse.setAuthorities(authorities);

        LogUtil.info(logprefix, location, "generated token", "");

        response.setSuccessStatus(HttpStatus.ACCEPTED);
        response.setData(authReponse);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity handleExceptionBadRequestException(HttpServletRequest request, MethodArgumentNotValidException e) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        LogUtil.warn(logprefix, location, "validation failed", "");
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());
        HttpReponse response = new HttpReponse(request.getRequestURI());
        response.setErrorStatus(HttpStatus.BAD_REQUEST);
        response.setData(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
