package com.kalsym.userservice.models;

import com.kalsym.userservice.models.daos.RoleAuthority;
import com.kalsym.userservice.models.daos.Client;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author user
 */
@Getter
@Setter
@ToString
public class UpdateSession {

    private String sessionId;
    private String tagKeyword;
    
}
