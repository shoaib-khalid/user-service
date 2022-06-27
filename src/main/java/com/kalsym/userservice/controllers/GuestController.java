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
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
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
    
    @PostMapping(name = "generate-session")
    @PreAuthorize("hasAnyAuthority('generate-session', 'all')")
    public ResponseEntity<HttpReponse> generateSession(HttpServletRequest request, @Valid @RequestBody GuestSession body) throws Exception {
        String logprefix = request.getRequestURI();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, body.toString(), "Generate Session for Guest:"+body.toString());
        
        body = guestSessionRepository.save(body);
                
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Guest Session created with id: " + body.getId(), "");
        response.setStatus(HttpStatus.CREATED);
        response.setData(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
