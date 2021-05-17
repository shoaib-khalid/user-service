package com.kalsym.userservice.controllers;

import com.kalsym.userservice.UsersServiceApplication;
import com.kalsym.userservice.VersionHolder;
import com.kalsym.userservice.models.HttpReponse;
import com.kalsym.userservice.models.daos.Authority;
import com.kalsym.userservice.models.daos.UserChannel;
import com.kalsym.userservice.repositories.AuthoritiesRepository;
import com.kalsym.userservice.repositories.UserChannelsRepository;
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
@RequestMapping("/userChannels")
public class UserChannelsController {

    @Autowired
    UserChannelsRepository userChannelsRepository;



    @GetMapping(path = {""}, name = "userChannels-get")
    @PreAuthorize("hasAnyAuthority('userChannels-get', 'all')")
    public ResponseEntity<HttpReponse> getUserChannels(HttpServletRequest request,
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String channelName,
            @RequestParam(required = false) String parentUserChannelId,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "", "");

        UserChannel userChannel = new UserChannel();
        userChannel.setId(id);
        userChannel.setChannelName(channelName);
        userChannel.setUserId(userId);

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, userChannel + "", "");
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT);
        Example<UserChannel> example = Example.of(userChannel, matcher);

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "page: " + page + " pageSize: " + pageSize, "");
        Pageable pageable = PageRequest.of(page, pageSize);

        response.setSuccessStatus(HttpStatus.OK);
        response.setData(userChannelsRepository.findAll(example, pageable));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = {"/{id}"}, name = "userChannels-get-by-id")
    @PreAuthorize("hasAnyAuthority('userChannels-get-by-id', 'all')")
    public ResponseEntity<HttpReponse> getUserChannelById(HttpServletRequest request, @PathVariable String id) {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "", "");

        Optional<UserChannel> optUserChannel = userChannelsRepository.findById(id);

        if (!optUserChannel.isPresent()) {
            Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "userChannel not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "userChannel found", "");
        response.setSuccessStatus(HttpStatus.OK);
        response.setData(optUserChannel.get());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(path = {"/{id}"}, name = "userChannels-delete-by-id")
    @PreAuthorize("hasAnyAuthority('userChannels-delete-by-id', 'all')")
    public ResponseEntity<HttpReponse> deleteUserChannelById(HttpServletRequest request, @PathVariable String id) {
        String logprefix = request.getRequestURI();
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "", "");

        Optional<UserChannel> optUserChannel = userChannelsRepository.findById(id);

        if (!optUserChannel.isPresent()) {
            Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "userChannel not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "userChannel found", "");
        userChannelsRepository.delete(optUserChannel.get());

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "userChannel deleted", "");
        response.setSuccessStatus(HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping(path = {"/{id}"}, name = "userChannels-put-by-id")
    @PreAuthorize("hasAnyAuthority('userChannels-put-by-id', 'all')")
    public ResponseEntity<HttpReponse> putUserChannelById(HttpServletRequest request, @PathVariable String id, @RequestBody UserChannel body) {
        String logprefix = request.getRequestURI();
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, body.toString(), "");

        Optional<UserChannel> optUserChannel = userChannelsRepository.findById(id);

        if (!optUserChannel.isPresent()) {
            Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "userChannel not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "userChannel found", "");
        UserChannel userChannel = optUserChannel.get();
        List<String> errors = new ArrayList<>();

        List<UserChannel> userChannels = userChannelsRepository.findByUserId(body.getUserId());

        for (UserChannel existingUserChannel : userChannels) {
            if (!userChannel.equals(existingUserChannel)) {
                if (existingUserChannel.getRefId().equals(body.getRefId())) {
                    Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "userChannelId already exists", "");
                    response.setErrorStatus(HttpStatus.CONFLICT);
                    errors.add("userChannelId already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
            }

        }
        userChannel.update(body);

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "userChannel updated for id: " + id, "");
        response.setSuccessStatus(HttpStatus.ACCEPTED);
        response.setData(userChannelsRepository.save(userChannel));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(name = "userChannels-post")
    @PreAuthorize("hasAnyAuthority('userChannels-post', 'all')")
    public ResponseEntity<HttpReponse> postUserChannel(HttpServletRequest request, @Valid @RequestBody UserChannel body) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, body.toString(), "");

        List<UserChannel> userChannels = userChannelsRepository.findByUserId(body.getUserId());
        List<String> errors = new ArrayList<>();

        for (UserChannel existingUserChannel : userChannels) {
            if (existingUserChannel.getRefId().equals(body.getRefId())) {
                Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "userChannelId already exists", "");
                response.setErrorStatus(HttpStatus.CONFLICT);
                errors.add("userChannelId already exists");
                response.setData(errors);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

        }

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "userChannel created with id: " + body.getId(), "");
        response.setSuccessStatus(HttpStatus.CREATED);
        response.setData(userChannelsRepository.save(body));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity handleExceptionBadRequestException(HttpServletRequest request, MethodArgumentNotValidException e) {
        String logprefix = request.getRequestURI();
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
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
