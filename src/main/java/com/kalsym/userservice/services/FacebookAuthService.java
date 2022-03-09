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
import java.net.URI;
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

/**
 *
 * @author taufik
 */
@Service
public class FacebookAuthService {
    
    private static final String logprefix = "FacebookAuthService";
    
    @Value("${fb.verify.token.url:https://graph.facebook.com/debug_token}")
    private String fbVerifyTokenUrl;
    
    public Optional<FacebookUserInfo> getUserInfo(String accessToken, String userAccessToken) {
        
        try {
            RestTemplate restTemplate = new RestTemplate();
            final URIBuilder builder = new URIBuilder(fbVerifyTokenUrl);
            builder.addParameter("access_token", accessToken);
            builder.addParameter("input_token", userAccessToken);
            URI uri = builder.build();
        
            ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
            
            if (result.getStatusCode() == HttpStatus.OK) {
                JSONObject jsonObject = new JSONObject(result.getBody());
                Logger.application.info("Facebook result:"+jsonObject.toString());
                JSONObject data = jsonObject.getJSONObject("data");
                Logger.application.info("Facebook data:"+data.toString());
                String id = data.getString("user_id");
                String email = data.getString("email");
                return Optional.of(new FacebookUserInfo(id, email, email, email));
            } else {
                return Optional.empty();
            }
            
        } catch (Exception e) {
            Logger.application.error(Logger.pattern, UserServiceApplication.VERSION, logprefix, "message from RC " + e.getMessage());            
            return Optional.empty();
        }   
    }

    public static class FacebookUserInfo {
        public String id = "";
        public String email = "";
        public String firstName = "";
        public String lastName = "";

        public FacebookUserInfo(String id, String email, String firstName, String lastName) {
            this.id = id;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }
}
