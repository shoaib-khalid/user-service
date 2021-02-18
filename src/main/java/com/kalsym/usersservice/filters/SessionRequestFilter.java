package com.kalsym.usersservice.filters;

import com.kalsym.usersservice.VersionHolder;
import com.kalsym.usersservice.models.MySQLUserDetails;
import com.kalsym.usersservice.models.daos.Session;
import com.kalsym.usersservice.repositories.SessionsRepository;
import com.kalsym.usersservice.services.MySQLUserDetailsService;
import com.kalsym.usersservice.utils.DateTimeUtil;
import com.kalsym.usersservice.utils.Logger;
import java.io.IOException;
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
    SessionsRepository sessionRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String logprefix = request.getRequestURI() + " ";

        Logger.application.warn(Logger.pattern, VersionHolder.VERSION, "-------------" + logprefix + "-------------", "", "");

        final String requestTokenHeader = request.getHeader("Authorization");

        //Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "requestTokenHeader: " + requestTokenHeader, "");
        String sessionId = null;

        // Token is in the form "Bearer token". Remove Bearer word and get only the Token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            sessionId = requestTokenHeader.substring(7);
        } else {
            Logger.application.warn(Logger.pattern, VersionHolder.VERSION, logprefix, "token does not begin with Bearer String", "");
        }

        if (sessionId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "sessionId: " + sessionId, "");
            Optional<Session> optSession = sessionRepository.findById(sessionId);
            if (optSession.isPresent()) {
                Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "sessionId valid", "");
                Session session = optSession.get();
                long diff = 0;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date expiryTime = sdf.parse(session.getExpiry());
                    Date currentTime = sdf.parse(DateTimeUtil.currentTimestamp());

                    //Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "currentTime: " + currentTime.getTime() + " expiryTime: " + expiryTime.getTime(), "");
                    diff = expiryTime.getTime() - currentTime.getTime();
                } catch (Exception e) {
                    Logger.application.warn(Logger.pattern, VersionHolder.VERSION, logprefix, "error calculating time to session expiry", "");
                }
                Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "time to session expiry: " + diff + "ms", "");
                if (0 < diff) {
                    MySQLUserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(session.getUsername());

                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                } else {
                    Logger.application.warn(Logger.pattern, VersionHolder.VERSION, logprefix, "session expired", "");
                }

            } else {
                Logger.application.warn(Logger.pattern, VersionHolder.VERSION, logprefix, "sessionId not valid", "");
            }

        }
        chain.doFilter(request, response);
    }
}
