package com.kalsym.userservice.controllers;

import com.kalsym.userservice.UserServiceApplication;
import com.kalsym.userservice.models.Auth;
import com.kalsym.userservice.models.ChangePassword;
import com.kalsym.userservice.models.HttpReponse;
import com.kalsym.userservice.models.daos.Client;
import com.kalsym.userservice.models.daos.ClientSession;
import com.kalsym.userservice.models.daos.RoleAuthority;
import com.kalsym.userservice.models.daos.CustomerSession;
import com.kalsym.userservice.models.daos.Customer;
import com.kalsym.userservice.models.requestbodies.AuthenticationBody;
import com.kalsym.userservice.models.requestbodies.ValidateOauthRequest;
import com.kalsym.userservice.repositories.RoleAuthoritiesRepository;
import com.kalsym.userservice.repositories.CustomerSessionsRepository;
import com.kalsym.userservice.repositories.CustomersRepository;
import com.kalsym.userservice.services.AppleAuthService;
import com.kalsym.userservice.services.EmaiVerificationlHandler;
import com.kalsym.userservice.services.FacebookAuthService;
import com.kalsym.userservice.services.GoogleAuthService;
import com.kalsym.userservice.utils.DateTimeUtil;
import com.kalsym.userservice.utils.Logger;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
@RequestMapping("/customers")
public class CustomersController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomersRepository customersRepository;

    @Autowired
    RoleAuthoritiesRepository roleAuthoritiesRepository;

    @Autowired
    CustomerSessionsRepository customerSessionsRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Autowired
    EmaiVerificationlHandler emaiVerificationlHandler;

    @Value("${session.expiry:3600}")
    private int expiry;
    
    @Value("${customer.apple.login.redirect.url:https://customer.symplified.it/applelogin}")
    private String appleLoginRedirectUrl;
    
    @Autowired
    GoogleAuthService googleAuthService;
    
    @Autowired
    FacebookAuthService facebookAuthService;
    
    @Autowired
    AppleAuthService appleAuthService;
    
    @Value("${customer.cookie.domain:.symplified.it}")
    private String customerCookieDomain;
    
    @GetMapping(path = {"/"}, name = "customers-get")
    @PreAuthorize("hasAnyAuthority('customers-get', 'all')")
    public ResponseEntity<HttpReponse> getCustomers(HttpServletRequest request,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String roleId,
            @RequestParam(required = false) Boolean locked,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        String logprefix = request.getRequestURI();
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        Customer user = new Customer();
        user.setUsername(username);
        user.setEmail(email);
        user.setRoleId(roleId);
        user.setLocked(locked);

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, user + "", "");
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withIgnorePaths("locked")
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Customer> example = Example.of(user, matcher);

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "page: " + page + " pageSize: " + pageSize, "");
        Pageable pageable = PageRequest.of(page, pageSize);

        response.setStatus(HttpStatus.OK);
        response.setData(customersRepository.findAll(example, pageable));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(path = {"/{id}"}, name = "customers-get-by-id")
    @PreAuthorize("hasAnyAuthority('customers-get-by-id', 'all')")
    public ResponseEntity<HttpReponse> getCustomerById(HttpServletRequest request, 
            @PathVariable String id) {
        String logprefix = request.getRequestURI();
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        Optional<Customer> optCustomer = customersRepository.findById(id);

        if (!optCustomer.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user found", "");
        response.setStatus(HttpStatus.OK);
        response.setData(optCustomer.get());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping(path = {"/{id}"}, name = "customers-delete-by-id")
    @PreAuthorize("hasAnyAuthority('customers-delete-by-id', 'all')")
    public ResponseEntity<HttpReponse> deleteCustomerById(HttpServletRequest request, @PathVariable String id) {
        String logprefix = request.getRequestURI();
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        Optional<Customer> optCustomer = customersRepository.findById(id);

        if (!optCustomer.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user found", "");
        customersRepository.delete(optCustomer.get());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user deleted", "");
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(path = {"/{id}"}, name = "customers-put-by-id")
    @PreAuthorize("hasAnyAuthority('customers-put-by-id', 'all')")
    public ResponseEntity<HttpReponse> putCustomerById(HttpServletRequest request, @PathVariable String id, @RequestBody Customer body) {
        String logprefix = request.getRequestURI();
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, body.toString(), "");

        Optional<Customer> optCustomer = customersRepository.findById(id);

        if (!optCustomer.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user found", "");
        Customer user = optCustomer.get();
        List<String> errors = new ArrayList<>();

        List<Customer> customers = customersRepository.findAll();

        for (Customer existingCustomer : customers) {
            if (!user.equals(existingCustomer)) {
                if (existingCustomer.getUsername().equals(body.getUsername())) {
                    Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "username already exists", "");
                    response.setStatus(HttpStatus.CONFLICT);
                    errors.add("username already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
                if (existingCustomer.getEmail().equals(body.getEmail())) {
                    Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "email already exists", "");
                    response.setStatus(HttpStatus.CONFLICT);
                    errors.add("email already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
                if (null != body.getId()) {
                    if (existingCustomer.getEmail().equals(body.getEmail())) {
                        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "userId already exists", "");
                        response.setStatus(HttpStatus.CONFLICT);
                        errors.add("userId already exists");
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

        user.update(body);
        user.setUpdated(DateTimeUtil.currentTimestamp());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user updated for id: " + id, "");
        response.setStatus(HttpStatus.ACCEPTED);
        response.setData(customersRepository.save(user));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(path = "/register", name = "customers-post")
    //@PreAuthorize("hasAnyAuthority('customers-post', 'all')")
    public ResponseEntity<HttpReponse> postCustomer(HttpServletRequest request, @RequestBody Customer body) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, body.toString(), "");

        List<String> errors = new ArrayList<>();

        List<Customer> customers = customersRepository.findAll();

        for (Customer existingCustomer : customers) {
            if (existingCustomer.getUsername().equals(body.getUsername())) {
                Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "username already exists", "");
                response.setStatus(HttpStatus.CONFLICT);
                errors.add("username already exists");
                response.setData(errors);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            if (existingCustomer.getEmail().equals(body.getEmail())) {
                Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "email already exists", "");
                response.setStatus(HttpStatus.CONFLICT);
                errors.add("email already exists");
                response.setData(errors);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        }

        if (body.getPassword() != null) {
            String password = bcryptEncoder.encode(body.getPassword());
            body.setPassword(password);
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Set password:"+password);
        }
        body.setCreated(DateTimeUtil.currentTimestamp());
        body.setUpdated(DateTimeUtil.currentTimestamp());
        body.setLocked(false);
        body.setDeactivated(false);
        body = customersRepository.save(body);
        
        emaiVerificationlHandler.sendVerificationEmail(body, null);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user created with id: " + body.getId(), "");
        response.setStatus(HttpStatus.CREATED);
        response.setData(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(path = {"/{id}/email-verification/{code}/verify"}, name = "customer-email-verification-by-id")
    //@PreAuthorize("hasAnyAuthority('clients-get-by-id', 'all')")
    public ResponseEntity<HttpReponse> getClientVerify(HttpServletRequest request,
            @PathVariable String id,
            @PathVariable String code) {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        Optional<Customer> optCustomer = customersRepository.findById(id);

        if (!optCustomer.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customer not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customer found", "");

        boolean verified = emaiVerificationlHandler.verifyEmail(optCustomer.get(), code);

        if (!verified) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "cannot verify", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        response.setStatus(HttpStatus.OK);
        response.setData(optCustomer.get());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    //authentication
    @PostMapping(path = "/authenticate", name = "customers-authenticate")
    public ResponseEntity authenticateCustomer(@Valid @RequestBody AuthenticationBody body,
            HttpServletRequest request) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "body: " + body);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(body.getUsername()+",CUSTOMER", body.getPassword())
            );

        } catch (BadCredentialsException e) {
            Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "error validating customer", "", e);
            response.setStatus(HttpStatus.UNAUTHORIZED, "Bad Craedentiails");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (AuthenticationException e) {
            Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "error validating customer", "", e);
            response.setStatus(HttpStatus.UNAUTHORIZED, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customer authenticated", "");

        List<Customer> userList = customersRepository.findByUsernameOrEmail(body.getUsername(), body.getUsername());
        
        if (userList.size()==0) {
            Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Customer not found. Found record:"+userList.size());
            response.setStatus(HttpStatus.UNAUTHORIZED, "Bad Craedentiails");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } else {
            Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Customer with same email address. Found record:"+userList.size());
        }
        Customer user = userList.get(0);
        
        List<RoleAuthority> roleAuthories = roleAuthoritiesRepository.findByRoleId(user.getRoleId());
        ArrayList<String> authorities = new ArrayList<>();
        if (null != roleAuthories) {

            for (RoleAuthority roleAuthority : roleAuthories) {
                authorities.add(roleAuthority.getAuthorityId());
            }
        }

        CustomerSession session = new CustomerSession();
        session.setRemoteAddress(request.getRemoteAddr());
        session.setOwnerId(user.getId());
        session.setUsername(user.getUsername());
        session.setCreated(DateTimeUtil.currentTimestamp());
        session.setUpdated(DateTimeUtil.currentTimestamp());
        session.setExpiry(DateTimeUtil.expiryTimestamp(expiry));
        session.setStatus("ACTIVE");
        session.generateTokens();

        session = customerSessionsRepository.save(session);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "session created with id: " + session.getId(), "");

        session.setUpdated(null);
        session.setStatus(null);
        session.setRemoteAddress(null);
        session.setId(null);

        Auth authReponse = new Auth();
        authReponse.setSession(session);
        authReponse.setAuthorities(authorities);
        authReponse.setRole(user.getRoleId());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "generated token", "");

        response.setStatus(HttpStatus.ACCEPTED);
        response.setData(authReponse);
        
        //set extra headers
        HttpHeaders responseHeaders = new HttpHeaders();
        Date expiry = DateTimeUtil.expiryTimestamp(3600);
        //Date format : Wed, 13 Jan 2021 22:23:01 GMT
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
        String expiryTimestamp = formatter.format(expiry);
        
        if (body.getDomain()!=null) {
            customerCookieDomain = body.getDomain();
        }
        responseHeaders.add("Set-Cookie", 
                        "CustomerId="+user.getId()+"; Domain="+customerCookieDomain+"; Path=/; Expires="+expiryTimestamp+"; Secure;");
        responseHeaders.add("Set-Cookie", 
                        "AccessToken="+session.getAccessToken()+"; Domain="+customerCookieDomain+"; Path=/; Expires="+expiryTimestamp+"; ");
        responseHeaders.add("Set-Cookie", 
                        "RefreshToken="+session.getRefreshToken()+"; Domain="+customerCookieDomain+"; Path=/; Expires="+expiryTimestamp+"; Secure;");
        responseHeaders.add("access-control-expose-headers","Set-Cookie");
        
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Add custom httpHeaders in response");
        
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .headers(responseHeaders)
                .body(response);
    }

    @GetMapping(path = {"/{email}/password_reset"}, name = "customer-password_reset-post-by-id")
    public ResponseEntity<HttpReponse> postCustomerPasswordReset(HttpServletRequest request,
            @PathVariable String email,
            @RequestParam(required = false) String domain,
            @RequestParam(required = false) String reseturl
            ) {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        List<Customer> customerList = customersRepository.findByUsernameOrEmail(email, email);

        if (customerList.isEmpty()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customer not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customer found", "");
        
        Customer customer = null;
        try {
            customer = customerList.get(0);
            emaiVerificationlHandler.sendPasswordReset(customer, domain, reseturl);
        } catch (Exception e) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "error sending email ", "", e);

            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setError(e.toString());
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        response.setStatus(HttpStatus.OK);
        response.setData(customer);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    @PutMapping(path = {"/{id}/password/{code}/reset"}, name = "customer-password_reset-post-by-id")
    //@PreAuthorize("hasAnyAuthority('clients-get-by-id', 'all')")
    public ResponseEntity<HttpReponse> putCustomerPasswordReset(HttpServletRequest request,
            @PathVariable String id,
            @PathVariable String code,
            @RequestBody Client body) {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        Optional<Customer> optCustomer = customersRepository.findById(id);

        if (!optCustomer.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Customer not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Customer existingCustomer = optCustomer.get();

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Customer found", "");

        boolean verified = emaiVerificationlHandler.verifyPasswordReset(existingCustomer, code);

        if (!verified) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "cannot verify", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setError("Code not valid");
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        existingCustomer.setPassword(bcryptEncoder.encode(body.getPassword()));
        existingCustomer = customersRepository.save(existingCustomer);
        response.setStatus(HttpStatus.ACCEPTED);
        response.setData(existingCustomer);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity handleExceptionBadRequestException(HttpServletRequest request, MethodArgumentNotValidException e) {
        String logprefix = request.getRequestURI();
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "validation failed", "");
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());
        HttpReponse response = new HttpReponse(request.getRequestURI());
        response.setStatus(HttpStatus.BAD_REQUEST);
        response.setData(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
     //authentication
    @PostMapping(path = "/loginoauth", name = "customers-authenticate")
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
            Optional<AppleAuthService.AppleUserInfo> appleResult = appleAuthService.validateToken(body.getToken());
            if (appleResult.isPresent()) {
                //authenticated
                if (appleResult.get().isValid) {
                    userEmail = body.getEmail();
                    Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Token is valid");
                }
            }
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "userEmail:"+userEmail+" UserId from request:"+body.getUserId());
        }            
        
        if (userEmail==null) {
            Logger.application.error(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Authentication failed");
            response.setStatus(HttpStatus.UNAUTHORIZED, "Fail to validate token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client authenticated", "");

        List<Customer> customerList = customersRepository.findByUsernameOrEmail(userEmail, userEmail);
        
        Customer customer = null;
        if (customerList.isEmpty()) {
            //create new account for customer
            customer = new Customer();
            customer.setUsername(userEmail);
            customer.setRoleId("CUSTOMER");
            customer.setCreated(DateTimeUtil.currentTimestamp());
            customer.setUpdated(DateTimeUtil.currentTimestamp());
            customer.setLocked(false);
            customer.setDeactivated(false);
            customer = customersRepository.save(customer);
        } else {
            customer = customerList.get(0);
        }
        
        List<RoleAuthority> roleAuthories = roleAuthoritiesRepository.findByRoleId(customer.getRoleId());
        ArrayList<String> authorities = new ArrayList<>();
        if (null != roleAuthories) {
            for (RoleAuthority roleAuthority : roleAuthories) {
                authorities.add(roleAuthority.getAuthorityId());
            }
        }

        CustomerSession session = new CustomerSession();
        session.setRemoteAddress(request.getRemoteAddr());
        session.setOwnerId(customer.getId());
        session.setUsername(customer.getUsername());
        session.setCreated(DateTimeUtil.currentTimestamp());
        session.setUpdated(DateTimeUtil.currentTimestamp());
        session.setExpiry(DateTimeUtil.expiryTimestamp(expiry));
        session.setStatus("ACTIVE");
        session.generateTokens();

        session = customerSessionsRepository.save(session);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "session created with id: " + session.getId(), "");

        session.setUpdated(null);
        session.setStatus(null);
        session.setRemoteAddress(null);
        session.setId(null);

        Auth authReponse = new Auth();
        authReponse.setSession(session);
        authReponse.setAuthorities(authorities);
        authReponse.setRole(customer.getRoleId());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "generated token", "");

        response.setStatus(HttpStatus.ACCEPTED);
        response.setData(authReponse);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);        
    }
    
    //authentication
    @PostMapping(path = "/applecallback", name = "customers-authenticate")
    public ResponseEntity appleCallback(HttpServletRequest request,
            @RequestParam String state,
            @RequestParam String code,
            @RequestParam String id_token) throws Exception {
        String logprefix = request.getRequestURI();
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "state: " + state);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "code: " + code);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "id_token: " + id_token);
        
        //redirect to front-end url      
        return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(appleLoginRedirectUrl+"?state="+URLEncoder.encode(state,StandardCharsets.UTF_8.toString())+"&code="+URLEncoder.encode(code,StandardCharsets.UTF_8.toString())+"&id_token="+URLEncoder.encode(id_token,StandardCharsets.UTF_8.toString())))
        .build();
    }
    
    @PostMapping(path = "session/refresh", name = "customers-session-refresh")
    public ResponseEntity refreshSession(@Valid @RequestBody String refreshToken,
            HttpServletRequest request) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "refreshToken: [" + refreshToken + "]");

        CustomerSession session = customerSessionsRepository.findByRefreshToken(refreshToken);

        if (null == session) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "session not found in customerSession", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Optional<Customer> optCustomer = customersRepository.findById(session.getOwnerId());

        if (!optCustomer.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customer not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        List<RoleAuthority> roleAuthories = roleAuthoritiesRepository.findByRoleId(optCustomer.get().getRoleId());
        ArrayList<String> authorities = new ArrayList<>();
        if (null != roleAuthories) {

            for (RoleAuthority roleAuthority : roleAuthories) {
                authorities.add(roleAuthority.getAuthorityId());
            }
        }

        CustomerSession newSession = new CustomerSession();
        newSession.setRemoteAddress(request.getRemoteAddr());
        newSession.setOwnerId(optCustomer.get().getId());
        newSession.setUsername(optCustomer.get().getUsername());
        newSession.setCreated(DateTimeUtil.currentTimestamp());
        newSession.setUpdated(DateTimeUtil.currentTimestamp());
        newSession.setExpiry(DateTimeUtil.expiryTimestamp(expiry));
        newSession.setStatus("ACTIVE");
        newSession.generateTokens();

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "session: " + newSession, "");

        newSession = customerSessionsRepository.save(newSession);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "session created with id: " + newSession.getId(), "");

        newSession.setUpdated(null);
        newSession.setStatus(null);
        newSession.setRemoteAddress(null);
        newSession.setUsername(null);
        newSession.setId(null);

        Auth authReponse = new Auth();
        authReponse.setSession(newSession);
        authReponse.setAuthorities(authorities);
        authReponse.setRole(optCustomer.get().getRoleId());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "generated token", "");

        response.setStatus(HttpStatus.ACCEPTED);
        response.setData(authReponse);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
    
    
    @PutMapping(path = {"/{id}/changepassword"}, name = "customers-put-by-id")
    @PreAuthorize("hasAnyAuthority('customers-put-by-id', 'all')")
    public ResponseEntity<HttpReponse> changePasswordCustomerById(HttpServletRequest request, @PathVariable String id, @RequestBody ChangePassword body) {
        String logprefix = request.getRequestURI();

        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, body.toString(), "");

        Optional<Customer> optCustomer = customersRepository.findById(id);

        if (!optCustomer.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Customer not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Customer found", "");
        Customer customer = optCustomer.get();
        List<String> errors = new ArrayList<>();
        
        if (!body.getNewPassword().equals(body.getConfirmNewPassword())) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "new password is not same", "");
            response.setStatus(HttpStatus.CONFLICT, "Confirm new password not same");
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
        //verify current password
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(customer.getUsername()+",CUSTOMER", body.getCurrentPassword())
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
        
        Customer customerBody = new Customer();
        customerBody.setId(id);
        if (null != body.getNewPassword() && body.getNewPassword().length() > 0) {
            customerBody.setPassword(bcryptEncoder.encode(body.getNewPassword()));
        } else {
            customerBody.setPassword(null);
        }

        customer.update(customerBody);
        customer.setUpdated(DateTimeUtil.currentTimestamp());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Customer updated for id: " + id, "");
        response.setStatus(HttpStatus.ACCEPTED);
        response.setData(customersRepository.save(customer));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
