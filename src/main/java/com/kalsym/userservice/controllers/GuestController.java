/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package com.kalsym.userservice.controllers;

import com.kalsym.userservice.UserServiceApplication;
import com.kalsym.userservice.models.HttpReponse;
import com.kalsym.userservice.models.daos.GuestSession;
import com.kalsym.userservice.repositories.GuestSessionsRepository;
import com.kalsym.userservice.utils.Logger;
import com.kalsym.userservice.utils.DateTimeUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author taufik
 */
@RestController()
@RequestMapping("/guest")
public class GuestController {
    
    @Autowired
    GuestSessionsRepository guestSessionRepository;
    
    @Value("${guest.session.expiry:1800}")
    private int sessionValidityInSecond;
    
    @PostMapping(path = {"/generateSession"}, name = "generate-session")    
    public ResponseEntity<HttpReponse> generateSession(HttpServletRequest request, @Valid @RequestBody GuestSession body) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, body.toString(), "Generate Session for Guest:"+body.toString());
        
        if (body.getId()!=null) {
            //update updated
            Optional<GuestSession> currentSession = guestSessionRepository.findById(body.getId());
            if (currentSession.isPresent()) {
                GuestSession session = currentSession.get();
                session.setUpdated(body.getUpdated());
                body = guestSessionRepository.save(session);        
            } else {
                //create new
                body.setExpiryTime(DateTimeUtil.expiryTimestamp(sessionValidityInSecond));
                body = guestSessionRepository.save(body); 
            }
        } else {
            //create new
            body.setExpiryTime(DateTimeUtil.expiryTimestamp(sessionValidityInSecond));
            body = guestSessionRepository.save(body);        
        }
                
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Guest Session created with id: " + body.getId(), "");
        response.setStatus(HttpStatus.CREATED);
        response.setData(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    
    @PutMapping(path = {"/updateSessionEmail"}, name = "update-session")    
    public ResponseEntity<HttpReponse> updateSessionEmail(HttpServletRequest request, @Valid @RequestBody GuestSession body) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, body.toString(), "Update Email for Session :"+body.toString());
        
        if (body.getId()!=null) {
            //update updated
            Optional<GuestSession> currentSession = guestSessionRepository.findById(body.getId());
            if (currentSession.isPresent()) {
                GuestSession session = currentSession.get();
                session.setUpdated(body.getUpdated());
                session.setEmail(body.getEmail());
                body = guestSessionRepository.save(session);  
                                          
                Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Guest Session updated with id: " + body.getId(), "");
                response.setStatus(HttpStatus.CREATED);
                response.setData(body);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                //not found
                Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Guest Session not found with id: " + body.getId(), "");
                response.setStatus(HttpStatus.EXPECTATION_FAILED);
                response.setData(body);
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
            }
        } else {
            //not found
            Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Guest Session not found with id: " + body.getId(), "");
            response.setStatus(HttpStatus.EXPECTATION_FAILED);
            response.setData(body);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
        }
      
    }

}
