package com.kalsym.userservice.controllers;

import com.kalsym.userservice.UserServiceApplication;
import com.kalsym.userservice.models.HttpReponse;
import com.kalsym.userservice.models.daos.AppToken;
import com.kalsym.userservice.repositories.AppTokensRepository;
import com.kalsym.userservice.utils.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/apptokens")
public class AppTokensController {

    @Autowired
    AppTokensRepository apptokensRepository;

    

    @GetMapping(path = {""}, name = "apptokens-get")
    @PreAuthorize("hasAnyAuthority('apptokens-get', 'all')")
    public ResponseEntity<HttpReponse> getAppTokens(HttpServletRequest request,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        AppToken apptoken = new AppToken();
        apptoken.setUserId(userId);
       

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, apptoken + "", "");
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<AppToken> example = Example.of(apptoken, matcher);

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "page: " + page + " pageSize: " + pageSize, "");
        Pageable pageable = PageRequest.of(page, pageSize);

        response.setStatus(HttpStatus.OK);
        response.setData(apptokensRepository.findAll(example, pageable));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = {"/{id}"}, name = "apptokens-get-by-id")
    @PreAuthorize("hasAnyAuthority('apptokens-get-by-id', 'all')")
    public ResponseEntity<HttpReponse> getAppTokenById(HttpServletRequest request, @PathVariable String id) {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        Optional<AppToken> optAppToken = apptokensRepository.findById(id);

        if (!optAppToken.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "apptoken not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "apptoken found", "");
        response.setStatus(HttpStatus.OK);
        response.setData(optAppToken.get());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(path = {"/{id}"}, name = "apptokens-delete-by-id")
    @PreAuthorize("hasAnyAuthority('apptokens-delete-by-id', 'all')")
    public ResponseEntity<HttpReponse> deleteAppTokenById(HttpServletRequest request, @PathVariable String id) {
        String logprefix = request.getRequestURI();
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        Optional<AppToken> optAppToken = apptokensRepository.findById(id);

        if (!optAppToken.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "apptoken not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "apptoken found", "");
        apptokensRepository.delete(optAppToken.get());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "apptoken deleted", "");
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping(path = {"/{id}"}, name = "apptokens-put-by-id")
    @PreAuthorize("hasAnyAuthority('apptokens-put-by-id', 'all')")
    public ResponseEntity<HttpReponse> putAppTokenById(HttpServletRequest request, @PathVariable String id, @RequestBody AppToken body) {
        String logprefix = request.getRequestURI();
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, body.toString(), "");

        Optional<AppToken> optAppToken = apptokensRepository.findById(id);

        if (!optAppToken.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "apptoken not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "apptoken found", "");
        AppToken apptoken = optAppToken.get();
        List<String> errors = new ArrayList<>();


        apptoken.update(body);

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "apptoken updated for id: " + id, "");
        response.setStatus(HttpStatus.ACCEPTED);
        response.setData(apptokensRepository.save(apptoken));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(name = "apptokens-post")
    @PreAuthorize("hasAnyAuthority('apptokens-post', 'all')")
    public ResponseEntity<HttpReponse> postAppToken(HttpServletRequest request, @Valid @RequestBody AppToken body) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, body.toString(), "");

        

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "apptoken created with id: " + body.getAppId(), "");
        response.setStatus(HttpStatus.CREATED);
        response.setData(apptokensRepository.save(body));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
