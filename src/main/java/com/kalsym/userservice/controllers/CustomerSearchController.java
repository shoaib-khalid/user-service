package com.kalsym.userservice.controllers;

import com.kalsym.userservice.UserServiceApplication;
import com.kalsym.userservice.models.HttpReponse;
import com.kalsym.userservice.models.Error;
import com.kalsym.userservice.models.daos.CustomerSearchHistory;
import com.kalsym.userservice.models.daos.ErrorCode;
import com.kalsym.userservice.models.daos.Customer;
import com.kalsym.userservice.repositories.CustomerSearchRepository;
import com.kalsym.userservice.repositories.CustomersRepository;
import com.kalsym.userservice.repositories.ErrorCodeRepository;
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
import org.springframework.data.domain.Sort;
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
@RequestMapping("/customer/{customerId}/search")
public class CustomerSearchController {

    @Autowired
    CustomerSearchRepository customerSearchRepository;
    
    @Autowired
    CustomersRepository customerRepository;
    
    @Autowired
    ErrorCodeRepository errorCodeRepository;

    @GetMapping(path = {""}, name = "customer-search-get")
    @PreAuthorize("hasAnyAuthority('customer-search-get', 'all')")
    public ResponseEntity<HttpReponse> getCustomerSearch(HttpServletRequest request,
            @PathVariable String customerId,
            @RequestParam(required = false) String storeId
            ) {
        String logprefix = request.getRequestURI();

        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        
        Optional<Customer> optCustomer = customerRepository.findById(customerId);
        if (!optCustomer.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customer not found", "");
            response.setStatus("US", "CUS", HttpStatus.NOT_FOUND, Error.RECORD_NOT_FOUND, errorCodeRepository);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
        CustomerSearchHistory customerSearch = new CustomerSearchHistory();
        customerSearch.setCustomerId(customerId);
        
        if (storeId!=null) {
            customerSearch.setStoreId(storeId);
        }
        
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, customerSearch + "", "");
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<CustomerSearchHistory> example = Example.of(customerSearch, matcher);
        
        int page=0;
        int pageSize=10;
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "page: " + page + " pageSize: " + pageSize, "");       
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("created").descending());

        response.setStatus("US", "CSS", HttpStatus.OK, Error.RECORD_FETCHED, errorCodeRepository);
        response.setData(customerSearchRepository.findAll(example, pageable));
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    @GetMapping(path = {"/{id}"}, name = "customer-search-get")
    @PreAuthorize("hasAnyAuthority('customer-search-get', 'all')")
    public ResponseEntity<HttpReponse> getCustomerSearchById(HttpServletRequest request,
            @PathVariable String customerId,
            @PathVariable String id) {
        String logprefix = request.getRequestURI();

        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        
        Optional<Customer> optCustomer = customerRepository.findById(customerId);
        if (!optCustomer.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customer not found", "");
            response.setStatus("US", "CUS", HttpStatus.NOT_FOUND, Error.RECORD_NOT_FOUND, errorCodeRepository);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
        Optional<CustomerSearchHistory> optCustomerSearch = customerSearchRepository.findById(id);
        if (optCustomerSearch.isPresent()) {
            response.setStatus("US", "CSS", HttpStatus.OK, Error.RECORD_FETCHED, errorCodeRepository);
            response.setData(optCustomerSearch.get());
        } else {
            response.setStatus("US", "CSS", HttpStatus.NOT_FOUND, Error.RECORD_NOT_FOUND, errorCodeRepository);
        }
        
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping(path = {"/{id}"}, name = "customer-search-delete-by-id")
    @PreAuthorize("hasAnyAuthority('customer-search-delete-by-id', 'all')")
    public ResponseEntity<HttpReponse> deleteCustomerSearchById(HttpServletRequest request,
            @PathVariable String customerId,
            @PathVariable String id) {
        String logprefix = request.getRequestURI();

        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        
        Optional<Customer> optCustomer = customerRepository.findById(customerId);
        if (!optCustomer.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customer not found", "");
            response.setStatus("US", "CUS", HttpStatus.NOT_FOUND, Error.RECORD_NOT_FOUND, errorCodeRepository);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
        Optional<CustomerSearchHistory> optCustomerSearch = customerSearchRepository.findById(id);

        if (!optCustomerSearch.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customerSearch not found", "");
            response.setStatus("US", "CSS", HttpStatus.NOT_FOUND, Error.RECORD_NOT_FOUND, errorCodeRepository);
            return ResponseEntity.status(response.getStatus()).body(response);
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customerSearch found", "");
        customerSearchRepository.delete(optCustomerSearch.get());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customerSearch deleted", "");
        response.setStatus("US", "CSS", HttpStatus.CREATED, Error.RECORD_DELETED, errorCodeRepository);
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    

    @PostMapping(name = "customer-search-post")
    @PreAuthorize("hasAnyAuthority('customer-search-post', 'all')")
    public ResponseEntity<HttpReponse> postCustomerSearch(HttpServletRequest request,
            @PathVariable String customerId,
            @Valid @RequestBody CustomerSearchHistory body) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, body.toString(), "");

        Optional<Customer> optCustomer = customerRepository.findById(customerId);
        if (!optCustomer.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customer not found", "");
            response.setStatus("US", "CUS", HttpStatus.NOT_FOUND, Error.RECORD_NOT_FOUND, errorCodeRepository);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
        //remove previous same text if any
        customerSearchRepository.deleteBySearchTextAndCustomerId(body.getSearchText(), customerId);
        
        body.setCustomerId(customerId);
        body = customerSearchRepository.save(body);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customerAddress created with id: " + body.getCustomerId(), "");
        
        List<CustomerSearchHistory> customerSearchHistoryList = customerSearchRepository.findByCustomerIdOrderByCreatedDesc(customerId);
        if (customerSearchHistoryList.size()>20) {
            int count=1;
            for (int i=0;i<customerSearchHistoryList.size();i++) {
                if (count>20) {
                    customerSearchRepository.deleteById(customerSearchHistoryList.get(i).getId());
                }
                count++;
            }
        }
        
        response.setStatus("US", "CSS", HttpStatus.CREATED, Error.RECORD_CREATED, errorCodeRepository);
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
