package com.kalsym.userservice.controllers;

import com.kalsym.userservice.UsersServiceApplication;
import com.kalsym.userservice.models.Auth;
import com.kalsym.userservice.models.HttpReponse;
import com.kalsym.userservice.models.daos.RoleAuthority;
import com.kalsym.userservice.models.daos.CustomerSession;
import com.kalsym.userservice.models.daos.Customer;
import com.kalsym.userservice.models.requestbodies.AuthenticationBody;
import com.kalsym.userservice.repositories.RoleAuthoritiesRepository;
import com.kalsym.userservice.repositories.CustomerSessionsRepository;
import com.kalsym.userservice.repositories.CustomersRepository;
import com.kalsym.userservice.services.EmaiVerificationlHandler;
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
@RequestMapping("/stores/{storeId}/customers")
public class StoreCustomersController {

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

    @GetMapping(path = {"/"}, name = "store-customers-get")
    @PreAuthorize("hasAnyAuthority('store-customers-get', 'all')")
    public ResponseEntity<HttpReponse> getCustomers(HttpServletRequest request,
            @PathVariable String storeId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String roleId,
            @RequestParam(required = false) Boolean locked,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        String logprefix = request.getRequestURI();

        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "", "");

        Customer user = new Customer();
        user.setUsername(username);
        user.setEmail(email);
        user.setRoleId(roleId);
        user.setLocked(locked);
        user.setStoreId(storeId);
        user.setPhoneNumber(phoneNumber);

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, user + "", "");
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withIgnorePaths("locked")
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Customer> example = Example.of(user, matcher);

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "page: " + page + " pageSize: " + pageSize, "");
        Pageable pageable = PageRequest.of(page, pageSize);

        response.setSuccessStatus(HttpStatus.OK);
        response.setData(customersRepository.findAll(example, pageable));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = {"/{id}"}, name = "store-customers-get-by-id")
    @PreAuthorize("hasAnyAuthority('store-customers-get-by-id', 'all')")
    public ResponseEntity<HttpReponse> getCustomerById(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String id) {
        String logprefix = request.getRequestURI();

        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "", "");

        Optional<Customer> optCustomer = customersRepository.findById(id);

        if (!optCustomer.isPresent()) {
            Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "user not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "user found", "");
        response.setSuccessStatus(HttpStatus.OK);
        response.setData(optCustomer.get());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(path = {"/{id}"}, name = "store-customers-delete-by-id")
    @PreAuthorize("hasAnyAuthority('store-customers-delete-by-id', 'all')")
    public ResponseEntity<HttpReponse> deleteCustomerById(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String id) {
        String logprefix = request.getRequestURI();

        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "", "");

        Optional<Customer> optCustomer = customersRepository.findById(id);

        if (!optCustomer.isPresent()) {
            Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "user not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "user found", "");
        customersRepository.delete(optCustomer.get());

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "user deleted", "");
        response.setSuccessStatus(HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping(path = {"/{id}"}, name = "store-customers-put-by-id")
    @PreAuthorize("hasAnyAuthority('store-customers-put-by-id', 'all')")
    public ResponseEntity<HttpReponse> putCustomerById(HttpServletRequest request,
            @PathVariable String storeId,
            @PathVariable String id, @RequestBody Customer body) {
        String logprefix = request.getRequestURI();

        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, body.toString(), "");

        Optional<Customer> optCustomer = customersRepository.findById(id);

        if (!optCustomer.isPresent()) {
            Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "user not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "user found", "");
        Customer user = optCustomer.get();
        List<String> errors = new ArrayList<>();

        List<Customer> customers = customersRepository.findAll();

        for (Customer existingCustomer : customers) {
            if (!user.equals(existingCustomer)) {
                if (existingCustomer.getUsername().equals(body.getUsername())) {
                    Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "username already exists", "");
                    response.setErrorStatus(HttpStatus.CONFLICT);
                    errors.add("username already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
                if (existingCustomer.getEmail().equals(body.getEmail())) {
                    Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "email already exists", "");
                    response.setErrorStatus(HttpStatus.CONFLICT);
                    errors.add("email already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
                if (null != body.getId()) {
                    if (existingCustomer.getEmail().equals(body.getEmail())) {
                        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "userId already exists", "");
                        response.setErrorStatus(HttpStatus.CONFLICT);
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

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "user updated for id: " + id, "");
        response.setSuccessStatus(HttpStatus.ACCEPTED);
        response.setData(customersRepository.save(user));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(path = "/register", name = "store-customers-post")
    //@PreAuthorize("hasAnyAuthority('store-customers-post', 'all')")
    public ResponseEntity<HttpReponse> postCustomer(HttpServletRequest request,
            @PathVariable String storeId,
            @Valid @RequestBody Customer body) throws Exception {
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

        List<Customer> customers = customersRepository.findByStoreId(storeId);

        for (Customer existingCustomer : customers) {
            if (existingCustomer.getUsername().equals(body.getUsername())) {
                Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "username already exists", "");
                response.setErrorStatus(HttpStatus.CONFLICT);
                errors.add("username already exists");
                response.setData(errors);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            if (existingCustomer.getEmail().equals(body.getEmail())) {
                Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "email already exists", "");
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
        body.setDeactivated(false);

        body = customersRepository.save(body);
        body.setPassword(null);
        emaiVerificationlHandler.sendVerificationEmail(body);
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "user created with id: " + body.getId(), "");
        response.setSuccessStatus(HttpStatus.CREATED);
        response.setData(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //authentication
    @PostMapping(path = "/authenticate", name = "store-customers-authenticate")
    public ResponseEntity authenticateCustomer(@Valid @RequestBody AuthenticationBody body,
            @PathVariable String storeId,
            HttpServletRequest request) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "", "");

        Authentication auth = null;
        Customer user = null;
        try {

            user = customersRepository.findByUsernameAndStoreId(body.getUsername(), storeId);
//            auth = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(body.getUsername(), body.getPassword())
//            );

            boolean match = bcryptEncoder.matches(body.getPassword(), user.getPassword());

            if (!match) {
                Logger.application.warn(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "user password not valid");
                response.setErrorStatus(HttpStatus.UNAUTHORIZED, "Bad Craedentiails");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

        } catch (BadCredentialsException e) {
            Logger.application.warn(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "error validating user", "", e);
            response.setErrorStatus(HttpStatus.UNAUTHORIZED, "Bad Craedentiails");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (AuthenticationException e) {
            Logger.application.warn(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "error validating user", "", e);
            response.setErrorStatus(HttpStatus.UNAUTHORIZED, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "user authenticated", "");

//        Customer user = customersRepository.findByUsernameOrEmail(body.getUsername(), body.getUsername());

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
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "session created with id: " + session.getId(), "");

        session.setUpdated(null);
        session.setStatus(null);
        session.setRemoteAddress(null);
        session.setId(null);

        Auth authReponse = new Auth();
        authReponse.setSession(session);
        authReponse.setAuthorities(authorities);
        authReponse.setRole(user.getRoleId());

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
