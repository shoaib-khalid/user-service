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
public class RefreshTokenRequest {

    @NotBlank(message = "fcmToken is required")
    private String fcmToken;
    
}
