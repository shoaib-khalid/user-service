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
    
    
    public Optional<AppleUserInfo> validateToken(String userAccessToken) {
        
        return Optional.of(new AppleUserInfo(true));
        
        /*
        try {
           
            RestTemplate restTemplate = new RestTemplate();
            final URIBuilder builder = new URIBuilder(fbVerifyTokenUrl);
            builder.addParameter("access_token", fbAppId+"|"+fbAppSecret);
            builder.addParameter("input_token", userAccessToken);
            URI uri = builder.build();
            
            Logger.application.info("Calling getUserInfo FB url:"+uri.toString());
            ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
            
            if (result.getStatusCode() == HttpStatus.OK) {
                JSONObject jsonObject = new JSONObject(result.getBody());
                Logger.application.info("Facebook result:"+jsonObject.toString());
                JSONObject data = jsonObject.getJSONObject("data");
                //Logger.application.info("Facebook data:"+data.toString());
                Boolean isValid = data.getBoolean("is_valid");
                String userId = null;
                if (isValid) {
                    userId = data.getString("user_id");
                    Logger.application.info("Token is valid. UserId:"+userId);
                }
                return Optional.of(new AppleUserInfo(userId));
            } else {
                return Optional.empty();
            }
            
        } catch (Exception e) {
            Logger.application.error(Logger.pattern, UserServiceApplication.VERSION, logprefix, "message from RC " + e.getMessage());            
            return Optional.empty();
        }  */ 
    }

    public static class AppleUserInfo {
        public boolean isValid=false;
        
        public AppleUserInfo(boolean isValid) {
            this.isValid = isValid;
        }
    }
}
