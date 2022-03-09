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
    
    @NotBlank(message = "name is required")
    private String name;
    
    private String userId;
    
    private String email;

}
