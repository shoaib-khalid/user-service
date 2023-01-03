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
public class ShiftBody {

    @NotBlank(message = "userId is required")
    private String userId;

    
}
