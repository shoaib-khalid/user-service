package com.kalsym.userservice.models.storeagent;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author saros
 */
@Getter
@Setter
@ToString
public class LiveChatStoreAgent {

    private String email;
    private String name;
    private String password;
    private String username;
    List<String> roles;
    CustomFields customFields;
}
