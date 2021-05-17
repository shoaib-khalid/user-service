package com.kalsym.userservice.models.storeagent;

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author saros
 */
@Getter
@Setter
@ToString
public class StoreAgentResponse {

    public String _id;
    public Date createdAt;
    public String username;
    public List<Object> emails;
    public String type;
    public String status;
    public boolean active;
    public Date _updatedAt;
    public List<String> roles;
    public String name;
    public Object settings;

}

//@Getter
//@Setter
//@NoArgsConstructor
//public class Email {
//
//    public String address;
//    public Boolean verified;
//}
//
//@Getter
//@Setter
//@NoArgsConstructor
//public class Settings {
//}
