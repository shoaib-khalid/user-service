package com.kalsym.userservice.filters;

import com.kalsym.userservice.models.daos.AdministratorSession;
import com.kalsym.userservice.models.daos.CustomerSession;
import com.kalsym.userservice.models.daos.ClientSession;
import com.kalsym.userservice.UserServiceApplication;
import com.kalsym.userservice.models.MySQLUserDetails;
import com.kalsym.userservice.repositories.AdministratorSessionsRepository;
import com.kalsym.userservice.repositories.ClientSessionsRepository;
import com.kalsym.userservice.repositories.CustomerSessionsRepository;
import com.kalsym.userservice.services.MySQLUserDetailsService;
import com.kalsym.userservice.utils.DateTimeUtil;
import com.kalsym.userservice.utils.Logger;
import java.io.IOException;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 * @author Sarosh
 */
@Component
public class SessionRequestFilter extends OncePerRequestFilter {

    @Autowired
    private MySQLUserDetailsService jwtUserDetailsService;

    @Autowired
    AdministratorSessionsRepository administratorSessionsRepository;

    @Autowired
    ClientSessionsRepository clientSessionsRepository;

    @Autowired
    CustomerSessionsRepository customerSessionsRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String logprefix = request.getRequestURI();

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, "-------------" + logprefix + "-------------", "", "");

        final String authHeader = request.getHeader("Authorization");
        Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Authorization: " + authHeader, "");

        String accessToken = null;

        boolean tokenPresent = false;

        // Token is in the form "Bearer token". Remove Bearer word and get only the Token
        if (null != authHeader && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.replace("Bearer ", "");
            Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "token: " + accessToken, "");
            Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "token length: " + accessToken.length(), "");
            tokenPresent = true;
        } else {
            Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "token does not begin with Bearer String", "");
        }

        boolean authorized = false;
        if (accessToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "sessionId: " + sessionId, "");
            AdministratorSession adminSesion = administratorSessionsRepository.findByAccessToken(accessToken);
            ClientSession clientSession = clientSessionsRepository.findByAccessToken(accessToken);
            CustomerSession customerSession = customerSessionsRepository.findByAccessToken(accessToken);

            Date expiryTime = null;
            
            String accessType = "CLIENT";
            String username = null;
            if (null == adminSesion
                    && null == clientSession
                    && null == customerSession) {
                Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "sessionId not valid", "");

            } else if (null != adminSesion) {
                Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "sessionId valid for admin_session", "");

                expiryTime = adminSesion.getExpiry();

                username = adminSesion.getUsername();
                accessType = "ADMIN";
            } else if (null != clientSession) {
                Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "sessionId valid for client_session", "");
                expiryTime = clientSession.getExpiry();

                username = clientSession.getUsername();
                accessType = "CLIENT";
            } else if (null != customerSession) {
                Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "sessionId valid for customer_session", "");
                expiryTime = customerSession.getExpiry();

                username = customerSession.getUsername();
                accessType = "CUSTOMER";
            }

            if (null != expiryTime && null != username) {
                long diff = 0;
                Date currentTime = DateTimeUtil.currentTimestamp();
                diff = expiryTime.getTime() - currentTime.getTime();
                Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "time to session expiry: " + diff + "ms", "");
                if (0 < diff) {
                    MySQLUserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username+","+accessType);

                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    authorized = true;
                } else {
                    Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "session expired", "");
                }
            }
        }

        Logger.cdr.info(request.getRemoteAddr() + "," + request.getMethod() + "," + request.getRequestURI() + "," + tokenPresent + "," + authorized);

        chain.doFilter(request, response);
    }
}
