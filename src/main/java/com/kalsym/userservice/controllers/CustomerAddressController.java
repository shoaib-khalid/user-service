package com.kalsym.userservice.controllers;

import com.kalsym.userservice.UserServiceApplication;
import com.kalsym.userservice.models.HttpReponse;
import com.kalsym.userservice.models.daos.CustomerAddress;
import com.kalsym.userservice.repositories.CustomerAddressRepository;
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
@RequestMapping("/customer/{customerId}/address")
public class CustomerAddressController {

    @Autowired
    CustomerAddressRepository customerAddressRepository;

    @GetMapping(path = {""}, name = "customer-address-get")
    @PreAuthorize("hasAnyAuthority('customer-address-get', 'all')")
    public ResponseEntity<HttpReponse> getCustomerAddresss(HttpServletRequest request,
            @PathVariable String customerId,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        String logprefix = request.getRequestURI();

        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        CustomerAddress customerAddress = new CustomerAddress();
        customerAddress.setCustomerId(customerId);

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, customerAddress + "", "");
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<CustomerAddress> example = Example.of(customerAddress, matcher);

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "page: " + page + " pageSize: " + pageSize, "");
        Pageable pageable = PageRequest.of(page, pageSize);

        response.setStatus(HttpStatus.OK);
        response.setData(customerAddressRepository.findAll(example, pageable));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping(path = {""}, name = "customer-address-delete-by-id")
    @PreAuthorize("hasAnyAuthority('customer-address-delete-by-id', 'all')")
    public ResponseEntity<HttpReponse> deleteCustomerAddressById(HttpServletRequest request,
            @PathVariable String customerId) {
        String logprefix = request.getRequestURI();

        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        Optional<CustomerAddress> optCustomerAddress = customerAddressRepository.findById(customerId);

        if (!optCustomerAddress.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customerAddress not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customerAddress found", "");
        customerAddressRepository.delete(optCustomerAddress.get());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customerAddress deleted", "");
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(path = {""}, name = "customer-address-put-by-id")
    @PreAuthorize("hasAnyAuthority('customer-address-put-by-id', 'all')")
    public ResponseEntity<HttpReponse> putCustomerAddressById(HttpServletRequest request,
            @PathVariable String customerId,
            @RequestBody CustomerAddress body) {
        String logprefix = request.getRequestURI();

        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, body.toString(), "");

        Optional<CustomerAddress> optCustomerAddress = customerAddressRepository.findById(customerId);

        if (!optCustomerAddress.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customerAddress not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        body.setCustomerId(customerId);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customerAddress found", "");
        CustomerAddress customerAddress = optCustomerAddress.get();

        customerAddress.update(body);

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customerAddress updated for id: " + customerId, "");
        response.setStatus(HttpStatus.ACCEPTED);
        response.setData(customerAddressRepository.save(customerAddress));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(name = "customer-address-post")
    //@PreAuthorize("hasAnyAuthority('customer-address-post', 'all')")
    public ResponseEntity<HttpReponse> postCustomerAddress(HttpServletRequest request,
            @PathVariable String customerId,
            @Valid @RequestBody CustomerAddress body) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, body.toString(), "");


        body.setId(customerId);
        body = customerAddressRepository.save(body);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customerAddress created with id: " + body.getCustomerId(), "");
        response.setStatus(HttpStatus.CREATED);
        response.setData(body);
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
