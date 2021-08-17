package com.kalsym.userservice.controllers;

import com.kalsym.userservice.UserServiceApplication;
import com.kalsym.userservice.models.HttpReponse;
import com.kalsym.userservice.models.daos.ClientPaymentDetail;
import com.kalsym.userservice.utils.DateTimeUtil;
import com.kalsym.userservice.utils.Logger;
import com.kalsym.userservice.repositories.ClientPaymentDetailsRepository;
import com.kalsym.userservice.repositories.ClientsRepository;
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
@RequestMapping("/clients/{clientId}/payment_details")
public class ClientPaymentDetailsController {
    
    @Autowired
    ClientPaymentDetailsRepository clientPaymentDetailsRepository;
    
    @Autowired
    ClientsRepository clientsRepository;
    
    @GetMapping(path = {"/"}, name = "client-payment-details-get")
    @PreAuthorize("hasAnyAuthority('client-payment-details-get', 'all')")
    public ResponseEntity<HttpReponse> getClientPaymentDetails(HttpServletRequest request,
            @PathVariable String clientId,
            @RequestParam(required = false) String bankName,
            @RequestParam(required = false) String bankAccountTitle,
            @RequestParam(required = false) String bankAccountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        String logprefix = request.getRequestURI();
        
        HttpReponse response = new HttpReponse(request.getRequestURI());
        
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        
        ClientPaymentDetail clientPaymentDetail = new ClientPaymentDetail();
        clientPaymentDetail.setBankAccountTitle(bankAccountTitle);
        clientPaymentDetail.setBankAccountNumber(bankAccountNumber);
        clientPaymentDetail.setBankName(bankName);
        clientPaymentDetail.setClientId(clientId);
        
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, clientPaymentDetail + "", "");
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withIgnorePaths("locked")
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<ClientPaymentDetail> example = Example.of(clientPaymentDetail, matcher);
        
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "page: " + page + " pageSize: " + pageSize, "");
        Pageable pageable = PageRequest.of(page, pageSize);
        
        response.setStatus(HttpStatus.OK);
        response.setData(clientPaymentDetailsRepository.findAll(example, pageable));
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    @GetMapping(path = {"/{id}"}, name = "client-payment-details-get-by-id")
    @PreAuthorize("hasAnyAuthority('client-payment-details-get-by-id', 'all')")
    public ResponseEntity<HttpReponse> getClientPaymentDetailById(HttpServletRequest request,
            @PathVariable String clientId,
            @PathVariable String id) {
        String logprefix = request.getRequestURI();
        
        HttpReponse response = new HttpReponse(request.getRequestURI());
        
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        
        Optional<ClientPaymentDetail> optClientPaymentDetail = clientPaymentDetailsRepository.findById(id);
        
        if (!optClientPaymentDetail.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "clientPaymentDetail not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "clientPaymentDetail found", "");
        response.setStatus(HttpStatus.OK);
        response.setData(optClientPaymentDetail.get());
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    @DeleteMapping(path = {"/{id}"}, name = "client-payment-details-delete-by-id")
    @PreAuthorize("hasAnyAuthority('client-payment-details-delete-by-id', 'all')")
    public ResponseEntity<HttpReponse> deleteClientPaymentDetailById(HttpServletRequest request,
            @PathVariable String clientId,
            @PathVariable String id) {
        String logprefix = request.getRequestURI();
        
        HttpReponse response = new HttpReponse(request.getRequestURI());
        
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        
        Optional<ClientPaymentDetail> optClientPaymentDetail = clientPaymentDetailsRepository.findById(id);
        
        if (!optClientPaymentDetail.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "clientPaymentDetail not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "clientPaymentDetail found", "");
        clientPaymentDetailsRepository.delete(optClientPaymentDetail.get());
        
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "clientPaymentDetail deleted", "");
        response.setStatus(HttpStatus.OK);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    @PutMapping(path = {"/{id}"}, name = "client-payment-details-put-by-id")
    @PreAuthorize("hasAnyAuthority('client-payment-details-put-by-id', 'all')")
    public ResponseEntity<HttpReponse> putClientPaymentDetailById(HttpServletRequest request,
            @PathVariable String clientId,
            @PathVariable String id, @RequestBody ClientPaymentDetail body) {
        String logprefix = request.getRequestURI();
        
        HttpReponse response = new HttpReponse(request.getRequestURI());
        
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, body.toString(), "");
        
        Optional<ClientPaymentDetail> optClientPaymentDetail = clientPaymentDetailsRepository.findById(id);
        
        if (!optClientPaymentDetail.isPresent()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "clientPaymentDetail not found", "");
            response.setStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "clientPaymentDetail found", "");
        ClientPaymentDetail clientPaymentDetail = optClientPaymentDetail.get();
        List<String> errors = new ArrayList<>();
        
        clientPaymentDetail.update(body);
        clientPaymentDetail.setUpdated(DateTimeUtil.currentTimestamp());
        
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "clientPaymentDetail updated for id: " + id, "");
        response.setStatus(HttpStatus.ACCEPTED);
        response.setData(clientPaymentDetailsRepository.save(clientPaymentDetail));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
    
    @PostMapping(path = "", name = "client-payment-details-post")
    //@PreAuthorize("hasAnyAuthority('client-payment-details-post', 'all')")
    public ResponseEntity<HttpReponse> postClientPaymentDetail(HttpServletRequest request,
            @PathVariable String clientId,
            @Valid @RequestBody ClientPaymentDetail body) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());
        
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, body.toString(), "");
        
        body.setCreated(DateTimeUtil.currentTimestamp());
        body.setUpdated(DateTimeUtil.currentTimestamp());
        body.setClientId(clientId);
        
        body = clientPaymentDetailsRepository.save(body);
        
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "clientPaymentDetail created with id: " + body.getId(), "");
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
