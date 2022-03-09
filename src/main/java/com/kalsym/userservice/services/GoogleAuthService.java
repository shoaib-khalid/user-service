package com.kalsym.userservice.services;

import com.kalsym.userservice.models.storeagent.StoreAgentResponse;
import com.kalsym.userservice.models.storeagent.LiveChatResponse;
import com.kalsym.userservice.models.storeagent.LiveChatStoreAgent;
import com.kalsym.userservice.utils.Logger;
import com.kalsym.userservice.UserServiceApplication;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.Conflict;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.common.collect.ImmutableList;

import java.util.Optional;

/**
 *
 * @author saros
 */
@Service
public class GoogleAuthService {

    private static final JacksonFactory jacksonFactory = new JacksonFactory();
    private static final HttpTransport httpTransport = new NetHttpTransport();
    private static final String logprefix = "GoogleAuthService";
   
    public Optional<GoogleUserInfo> getUserInfo(String token, String clientId) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                .Builder(httpTransport, jacksonFactory)
                .setAudience(ImmutableList.of(clientId))
                .build();

        try {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Verify token -> clientId:"+clientId+" Token:"+token);
            GoogleIdToken verifiedToken = verifier.verify(token);
            GoogleIdToken.Payload tokenPayload = verifiedToken.getPayload();
            String subject = tokenPayload.getSubject();
            String email = tokenPayload.getEmail();
            String firstName = (String) tokenPayload.get("given_name");
            String lastName = (String) tokenPayload.get("family_name");
            String locale = (String) tokenPayload.get("locale");
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Return from google -> email:"+email+" firstName:"+firstName+" lastName:"+lastName+" locale:"+locale );
            return Optional.of(new GoogleUserInfo(subject, email, firstName, lastName));
        } catch (Exception ex) {
            Logger.application.error(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Exception during verify token :",ex );
            return Optional.empty();
        }
    }

    public static class GoogleUserInfo {
        public String id = "";
        public String email = "";
        public String firstName = "";
        public String lastName = "";

        public GoogleUserInfo(String id, String email, String firstName, String lastName) {
            this.id = id;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

}
