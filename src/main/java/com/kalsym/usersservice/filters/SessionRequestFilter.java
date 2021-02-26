package com.kalsym.usersservice.filters;

import com.kalsym.usersservice.UsersServiceApplication;
import com.kalsym.usersservice.VersionHolder;
import com.kalsym.usersservice.models.MySQLUserDetails;
import com.kalsym.usersservice.models.daos.*;
import com.kalsym.usersservice.repositories.AdministratorSessionsRepository;
import com.kalsym.usersservice.repositories.ClientSessionsRepository;
import com.kalsym.usersservice.repositories.CustomerSessionsRepository;
import com.kalsym.usersservice.services.MySQLUserDetailsService;
import com.kalsym.usersservice.utils.DateTimeUtil;
import com.kalsym.usersservice.utils.Logger;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
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

        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, "-------------" + logprefix + "-------------", "", "");

        final String authHeader = request.getHeader("Authorization");
        Logger.application.warn(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "Authorization: " + authHeader, "");

        String accessToken = null;

        // Token is in the form "Bearer token". Remove Bearer word and get only the Token
        if (null != authHeader && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.replace("Bearer ", "");
            Logger.application.warn(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "token: " + accessToken, "");
            Logger.application.warn(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "token length: " + accessToken.length(), "");

        } else {
            Logger.application.warn(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "token does not begin with Bearer String", "");
        }

        if (accessToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "sessionId: " + sessionId, "");
            AdministratorSession adminSesion = administratorSessionsRepository.findByAccessToken(accessToken);
            ClientSession clientSession = clientSessionsRepository.findByAccessToken(accessToken);
            CustomerSession customerSession = customerSessionsRepository.findByAccessToken(accessToken);

            Date expiryTime = null;

            String username = null;
            if (null == adminSesion
                    && null == clientSession
                    && null == customerSession) {
                Logger.application.warn(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "sessionId not valid", "");

            } else if (null != adminSesion) {
                Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "sessionId valid for admin_session", "");

                expiryTime = adminSesion.getExpiry();

                username = adminSesion.getUsername();

            } else if (null != clientSession) {
                Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "sessionId valid for client_session", "");
                expiryTime = clientSession.getExpiry();

                username = clientSession.getUsername();

            } else if (null != customerSession) {
                Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "sessionId valid for customer_session", "");
                expiryTime = customerSession.getExpiry();

                username = customerSession.getUsername();

            }

            if (null != expiryTime && null != username) {
                long diff = 0;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date currentTime = sdf.parse(DateTimeUtil.currentTimestamp());
                    diff = expiryTime.getTime() - currentTime.getTime();
                } catch (ParseException e) {
                    Logger.application.warn(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "error calculating time to session expiry", "");
                }
                Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "time to session expiry: " + diff + "ms", "");
                if (0 < diff) {
                    MySQLUserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                } else {
                    Logger.application.warn(Logger.pattern, UsersServiceApplication.VERSION, logprefix, "session expired", "");
                }
            }
        }
        chain.doFilter(request, response);
    }
}
