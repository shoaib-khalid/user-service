package com.kalsym.userservice.controllers;

import com.kalsym.userservice.UserServiceApplication;
import com.kalsym.userservice.models.Auth;
import com.kalsym.userservice.models.HttpReponse;
import com.kalsym.userservice.models.daos.AdministratorSession;
import com.kalsym.userservice.models.daos.RoleAuthority;
import com.kalsym.userservice.models.daos.Administrator;
import com.kalsym.userservice.models.requestbodies.AuthenticationBody;
import com.kalsym.userservice.repositories.AdministratorSessionsRepository;
import com.kalsym.userservice.repositories.RoleAuthoritiesRepository;
import com.kalsym.userservice.repositories.AdministratorsRepository;
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

/**
 *
 * @author Sarosh
 */
@RestController()
@RequestMapping("/administrators")
public class AdministratorsController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AdministratorsRepository administratorsRepository;

    @Autowired
    RoleAuthoritiesRepository roleAuthoritiesRepository;

    @Autowired
    AdministratorSessionsRepository administratorSessionsRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Value("${session.expiry:3600}")
    private int expiry;

    @GetMapping(path = {"/"}, name = "administrators-get")
    @PreAuthorize("hasAnyAuthority('administrators-get', 'all')")
    public ResponseEntity<HttpReponse> getAdministrators(HttpServletRequest request,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String roleId,
            @RequestParam(required = false) Boolean locked,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        Administrator user = new Administrator();
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
        Example<Administrator> example = Example.of(user, matcher);

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "page: " + page + " pageSize: " + pageSize, "");
        Pageable pageable = PageRequest.of(page, pageSize);

        response.setStatus(HttpStatus.OK);
        response.setData(administratorsRepository.findAll(example, pageable));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(path = {"/{id}"}, name = "administrators-get-by-id")
    @PreAuthorize("hasAnyAuthority('administrators-get-by-id', 'all')")
    public ResponseEntity<HttpReponse> getAdministratorById(HttpServletRequest request, @PathVariable String id) {
        String logprefix = request.getRequestURI();
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        Optional<Administrator> optAdministrator = administratorsRepository.findById(id);

        if (!optAdministrator.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user found", "");
        response.setStatus(HttpStatus.OK);
        response.setData(optAdministrator.get());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping(path = {"/{id}"}, name = "administrators-delete-by-id")
    @PreAuthorize("hasAnyAuthority('administrators-delete-by-id', 'all')")
    public ResponseEntity<HttpReponse> deleteAdministratorById(HttpServletRequest request, @PathVariable String id) {
        String logprefix = request.getRequestURI();
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        Optional<Administrator> optAdministrator = administratorsRepository.findById(id);

        if (!optAdministrator.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user found", "");
        administratorsRepository.delete(optAdministrator.get());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user deleted", "");
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(path = {"/{id}"}, name = "administrators-put-by-id")
    @PreAuthorize("hasAnyAuthority('administrators-put-by-id', 'all')")
    public ResponseEntity<HttpReponse> putAdministratorById(HttpServletRequest request, @PathVariable String id, @RequestBody Administrator body) {
        String logprefix = request.getRequestURI();
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, body.toString(), "");

        Optional<Administrator> optAdministrator = administratorsRepository.findById(id);

        if (!optAdministrator.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user found", "");
        Administrator user = optAdministrator.get();
        List<String> errors = new ArrayList<>();

        List<Administrator> administrators = administratorsRepository.findAll();

        for (Administrator existingAdministrator : administrators) {
            if (!user.equals(existingAdministrator)) {
                if (existingAdministrator.getUsername().equals(body.getUsername())) {
                    Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "username already exists", "");
                    response.setStatus(HttpStatus.CONFLICT);
                    errors.add("username already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
                if (existingAdministrator.getEmail().equals(body.getEmail())) {
                    Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "email already exists", "");
                    response.setStatus(HttpStatus.CONFLICT);
                    errors.add("email already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
                if (null != body.getId()) {
                    if (existingAdministrator.getEmail().equals(body.getEmail())) {
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
        response.setData(administratorsRepository.save(user));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(path = "/register", name = "administrators-post")
    @PreAuthorize("hasAnyAuthority('administrators-post', 'all')")
    public ResponseEntity<HttpReponse> postAdministrator(HttpServletRequest request, @Valid @RequestBody Administrator body) throws Exception {
        String logprefix = request.getRequestURI();
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, body.toString(), "");

        List<String> errors = new ArrayList<>();
        if (null == body.getPassword() || body.getPassword().length() == 0) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "username already exists", "");
            response.setStatus(HttpStatus.BAD_REQUEST);
            errors.add("password is required exists");
            response.setData(errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        List<Administrator> administrators = administratorsRepository.findAll();

        for (Administrator existingAdministrator : administrators) {
            if (existingAdministrator.getUsername().equals(body.getUsername())) {
                Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "username already exists", "");
                response.setStatus(HttpStatus.CONFLICT);
                errors.add("username already exists");
                response.setData(errors);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            if (existingAdministrator.getEmail().equals(body.getEmail())) {
                Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "email already exists", "");
                response.setStatus(HttpStatus.CONFLICT);
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
        body = administratorsRepository.save(body);
        body.setPassword(null);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user created with id: " + body.getId(), "");
        response.setStatus(HttpStatus.CREATED);
        response.setData(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //authentication
    @PostMapping(path = "/authenticate", name = "administrators-authenticate")
    public ResponseEntity authenticateAdministrator(@Valid @RequestBody AuthenticationBody body,
            HttpServletRequest request) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(body.getUsername()+",ADMIN", body.getPassword())
            );
        } catch (BadCredentialsException e) {
            Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "error validating user", "");
            response.setStatus(HttpStatus.UNAUTHORIZED, "Bad Craedentiails");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (AuthenticationException e) {
            Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "error validating user", "");
            response.setStatus(HttpStatus.UNAUTHORIZED, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user authenticated", "");

        Administrator user = administratorsRepository.findByUsernameOrEmail(body.getUsername(), body.getUsername());

        List<RoleAuthority> roleAuthories = roleAuthoritiesRepository.findByRoleId(user.getRoleId());
        ArrayList<String> authorities = new ArrayList<>();
        if (null != roleAuthories) {

            for (RoleAuthority roleAuthority : roleAuthories) {
                authorities.add(roleAuthority.getAuthorityId());
            }
        }

        AdministratorSession session = new AdministratorSession();
        session.setRemoteAddress(request.getRemoteAddr());
        session.setOwnerId(user.getId());
        session.setUsername(user.getUsername());
        session.setCreated(DateTimeUtil.currentTimestamp());
        session.setUpdated(DateTimeUtil.currentTimestamp());
        session.setExpiry(DateTimeUtil.expiryTimestamp(expiry));
        session.setStatus("ACTIVE");
        session.generateTokens();

        session = administratorSessionsRepository.save(session);
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
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
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
}
