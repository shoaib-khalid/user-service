package com.kalsym.userservice.controllers;

import com.kalsym.userservice.UserServiceApplication;
import com.kalsym.userservice.models.HttpReponse;
import com.kalsym.userservice.models.daos.Authority;
import com.kalsym.userservice.repositories.AuthoritiesRepository;
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
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Sarosh
 */
@RestController()
@RequestMapping("/authorities")
public class AuthoritiesController {

    @Autowired
    AuthoritiesRepository authoritiesRepository;

    @GetMapping(path = {"/"}, name = "authorities-get")
    @PreAuthorize("hasAnyAuthority('authorities-get', 'all')")
    public ResponseEntity<HttpReponse> getAuthorities(HttpServletRequest request) {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        response.setStatus(HttpStatus.OK);
        response.setData(authoritiesRepository.findAll());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = {"/{id}"}, name = "authorities-get-by-id")
    @PreAuthorize("hasAnyAuthority('authorities-get-by-id', 'all')")
    public ResponseEntity<HttpReponse> getAuthorityById(HttpServletRequest request, @PathVariable String id) {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        Optional<Authority> optAuthority = authoritiesRepository.findById(id);

        if (!optAuthority.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "authority not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "authority found", "");
        response.setStatus(HttpStatus.OK);
        response.setData(optAuthority.get());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(path = {"/{id}"}, name = "authorities-delete-by-id")
    @PreAuthorize("hasAnyAuthority('authorities-delete-by-id', 'all')")
    public ResponseEntity<HttpReponse> deleteAuthorityById(HttpServletRequest request, @PathVariable String id) {
        String logprefix = request.getRequestURI();
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        Optional<Authority> optAuthority = authoritiesRepository.findById(id);

        if (!optAuthority.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "authority not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "authority found", "");
        authoritiesRepository.delete(optAuthority.get());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "authority deleted", "");
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping(path = {"/{id}"}, name = "authorities-put-by-id")
    @PreAuthorize("hasAnyAuthority('authorities-put-by-id', 'all')")
    public ResponseEntity<HttpReponse> putAuthorityById(HttpServletRequest request, @PathVariable String id,
            @RequestBody Authority body) {
        String logprefix = request.getRequestURI();
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, body.toString(), "");

        Optional<Authority> optAuthority = authoritiesRepository.findById(id);

        if (!optAuthority.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "authority not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "authority found", "");
        Authority authority = optAuthority.get();
        List<String> errors = new ArrayList<>();

        List<Authority> authorities = authoritiesRepository.findAll();

        for (Authority existingAuthority : authorities) {
            if (!authority.equals(existingAuthority)) {
                if (existingAuthority.getId().equals(body.getId())) {
                    Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "authorityId already exists", "");
                    response.setStatus(HttpStatus.CONFLICT);
                    errors.add("authorityId already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
            }

        }
        authority.updateAuthority(body);

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "authority updated for id: " + body.getId(), "");
        response.setStatus(HttpStatus.ACCEPTED);
        response.setData(authoritiesRepository.save(authority));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(name = "authorities-post")
    @PreAuthorize("hasAnyAuthority('authorities-post', 'all')")
    public ResponseEntity<HttpReponse> postAuthority(HttpServletRequest request,
            @Valid @RequestBody Authority body) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, body.toString(), "");

        List<Authority> authorities = authoritiesRepository.findAll();
        List<String> errors = new ArrayList<>();

        for (Authority existingAuthority : authorities) {
            if (existingAuthority.getId().equals(body.getId())) {
                Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "authorityId already exists", "");
                response.setStatus(HttpStatus.CONFLICT);
                errors.add("authorityId already exists");
                response.setData(errors);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        }

        body = authoritiesRepository.save(body);

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "authority created with id: " + body.getId(), "");
        response.setStatus(HttpStatus.CREATED);
        response.setData(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(path = "/bulk", name = "authorities-post-bulk")
//    @PreAuthorize("hasAnyAuthority('authorities-post-bulk', 'all')")
    public ResponseEntity<HttpReponse> postAuthority(HttpServletRequest request,
            @RequestBody List<Authority> body) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, body.toString(), "");

        List<Authority> newAuthorities = new ArrayList<>();

        body.forEach(bodyAuthority -> {
            newAuthorities.add(authoritiesRepository.save(bodyAuthority));
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "saved authrity: " + bodyAuthority.getId(), "");

        });

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "authorities created count: " + body.size(), "");
        response.setStatus(HttpStatus.CREATED);
        response.setData(newAuthorities);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
