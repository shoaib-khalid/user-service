/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package com.kalsym.userservice.services;

import com.kalsym.userservice.UserServiceApplication;
import com.kalsym.userservice.models.storeagent.LiveChatResponse;
import com.kalsym.userservice.models.storeagent.LiveChatStoreAgent;
import com.kalsym.userservice.utils.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.net.URL;
import org.apache.http.client.utils.URIBuilder;
        
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import java.text.ParseException;

/**
 *
 * @author taufik
 */
@Service
public class AppleAuthService {
    
    private static final String logprefix = "AppleAuthService";
    
    @Value("${fb.verify.token.url:https://graph.facebook.com/debug_token}")
    private String fbVerifyTokenUrl;
    
    @Value("${fb.generate.access.token.url:https://graph.facebook.com/access_token}")
    private String fbGenerateAccessTokenUrl;
    
    @Value("${fb.verify.appid:283489330438468}")
    private String fbAppId;
    
    @Value("${fb.verify.appSecret:519e90e18180c6b69e1ec1013139e2e4}")
    private String fbAppSecret;
    
    
    public Optional<AppleUserInfo> validateToken(String appleIdentityToken) {
        
        try {
            JWKSet publicKeys = JWKSet.load(new URL("https://appleid.apple.com/auth/keys"));
            boolean validChain = false;
            int i=0;
            for (JWK key : publicKeys.getKeys()) {
                JWSObject jwt =  JWSObject.parse(appleIdentityToken);
                if (!validChain) {
                    validChain = jwt.verify(new RSASSAVerifier(key.toRSAKey()));
                    Logger.application.info("validChain["+i+"] = "+validChain);
                }  
                i++;
            }
            Logger.application.info("validChain = "+validChain);
            if (validChain) {
                return Optional.of(new AppleUserInfo(true));
            } else {
                return Optional.empty();
            }
        } catch (Exception ex) {
            Logger.application.error(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Exception : ", ex);            
            return Optional.empty();
        }     
    }

    public static class AppleUserInfo {
        public boolean isValid=false;
        
        public AppleUserInfo(boolean isValid) {
            this.isValid = isValid;
        }
    }
}
