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
public class TempTokenRequest {

    @NotBlank(message = "username is required")
    private String username;

    @NotBlank(message = "clientId is required")
    private String clientId;
}
