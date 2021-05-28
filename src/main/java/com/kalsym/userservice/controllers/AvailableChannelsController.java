package com.kalsym.userservice.controllers;

import com.kalsym.userservice.UserServiceApplication;
import com.kalsym.userservice.models.HttpReponse;
import com.kalsym.userservice.models.daos.AvailableChannel;
import com.kalsym.userservice.repositories.AvailableChannelsRepository;
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
@RequestMapping("/availablechannels")
public class AvailbleChannelsController {

    @Autowired
    AvailableChannelsRepository availableChannelsRepository;


    @GetMapping(path = {""}, name = "availablechannels-get")
    @PreAuthorize("hasAnyAuthority('availablechannels-get', 'all')")
    public ResponseEntity<HttpReponse> getAvailableChannels(HttpServletRequest request,
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String parentAvailableChannelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        AvailableChannel availablechannel = new AvailableChannel();
        availablechannel.setId(id);
        availablechannel.setName(name);
        

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, availablechannel + "", "");
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<AvailableChannel> example = Example.of(availablechannel, matcher);

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "page: " + page + " pageSize: " + pageSize, "");
        Pageable pageable = PageRequest.of(page, pageSize);

        response.setStatus(HttpStatus.OK);
        response.setData(availableChannelsRepository.findAll(example, pageable));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = {"/{id}"}, name = "availablechannels-get-by-id")
    @PreAuthorize("hasAnyAuthority('availablechannels-get-by-id', 'all')")
    public ResponseEntity<HttpReponse> getAvailableChannelById(HttpServletRequest request, @PathVariable String id) {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        Optional<AvailableChannel> optAvailableChannel = availableChannelsRepository.findById(id);

        if (!optAvailableChannel.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "availablechannel not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "availablechannel found", "");
        response.setStatus(HttpStatus.OK);
        response.setData(optAvailableChannel.get());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(path = {"/{id}"}, name = "availablechannels-delete-by-id")
    @PreAuthorize("hasAnyAuthority('availablechannels-delete-by-id', 'all')")
    public ResponseEntity<HttpReponse> deleteAvailableChannelById(HttpServletRequest request, @PathVariable String id) {
        String logprefix = request.getRequestURI();
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        Optional<AvailableChannel> optAvailableChannel = availableChannelsRepository.findById(id);

        if (!optAvailableChannel.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "availablechannel not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "availablechannel found", "");
        availableChannelsRepository.delete(optAvailableChannel.get());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "availablechannel deleted", "");
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping(path = {"/{id}"}, name = "availablechannels-put-by-id")
    @PreAuthorize("hasAnyAuthority('availablechannels-put-by-id', 'all')")
    public ResponseEntity<HttpReponse> putAvailableChannelById(HttpServletRequest request, @PathVariable String id, @RequestBody AvailableChannel body) {
        String logprefix = request.getRequestURI();
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, body.toString(), "");

        Optional<AvailableChannel> optAvailableChannel = availableChannelsRepository.findById(id);

        if (!optAvailableChannel.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "availablechannel not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "availablechannel found", "");
        AvailableChannel availablechannel = optAvailableChannel.get();
        List<String> errors = new ArrayList<>();

        List<AvailableChannel> availablechannels = availableChannelsRepository.findAll();

        for (AvailableChannel existingAvailableChannel : availablechannels) {
            if (!availablechannel.equals(existingAvailableChannel)) {
                if (existingAvailableChannel.getId().equals(body.getId())) {
                    Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "availablechannelId already exists", "");
                    response.setStatus(HttpStatus.CONFLICT);
                    errors.add("availablechannelId already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
                if (existingAvailableChannel.getName().equals(body.getName())) {
                    Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "name already exists", "");
                    response.setStatus(HttpStatus.CONFLICT);
                    errors.add("name already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
            }

        }
        availablechannel.update(body);

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "availablechannel updated for id: " + id, "");
        response.setStatus(HttpStatus.ACCEPTED);
        response.setData(availableChannelsRepository.save(availablechannel));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(name = "availablechannels-post")
    @PreAuthorize("hasAnyAuthority('availablechannels-post', 'all')")
    public ResponseEntity<HttpReponse> postAvailableChannel(HttpServletRequest request, @Valid @RequestBody AvailableChannel body) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, body.toString(), "");

        List<AvailableChannel> availablechannels = availableChannelsRepository.findAll();
        List<String> errors = new ArrayList<>();

        for (AvailableChannel existingAvailableChannel : availablechannels) {
            if (existingAvailableChannel.getId().equals(body.getId())) {
                Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "availablechannelId already exists", "");
                response.setStatus(HttpStatus.CONFLICT);
                errors.add("availablechannelId already exists");
                response.setData(errors);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            if (existingAvailableChannel.getName().equals(body.getName())) {
                Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "name already exists", "");
                response.setStatus(HttpStatus.CONFLICT);
                errors.add("name already exists");
                response.setData(errors);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "availablechannel created with id: " + body.getId(), "");
        response.setStatus(HttpStatus.CREATED);
        response.setData(availableChannelsRepository.save(body));
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
