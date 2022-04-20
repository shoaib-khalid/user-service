package com.kalsym.userservice.models.requestbodies;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Sarosh
 */
@ToString
@Getter
@Setter
public class ValidateOauthRequest {

    @NotBlank(message = "loginType is required")
    private String loginType;
    
    @NotBlank(message = "token is required")
    private String token;
        
    private String name;
    
    private String userId;
    
    private String email;
    
    @NotBlank(message = "country is required")
    private String country;
    
    private String domain;

}
