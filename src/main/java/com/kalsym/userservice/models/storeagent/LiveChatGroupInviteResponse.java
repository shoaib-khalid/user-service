/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package com.kalsym.userservice.models.storeagent;

import java.util.Date;
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
public class LiveChatGroupInviteResponse {

    public Group group;
    public boolean success;
}

@Getter
@Setter
@ToString
class Group {

    public String _id;
    public Date ts;
    public String t;
    public String name;
    public List<String> usernames;
    public U u;
    public int msgs;
    public Date _updatedAt;
    public Date lm;
}

@Getter
@Setter
@ToString
class Root {

    public Group group;
    public boolean success;
}

@Getter
@Setter
@ToString
class U {

    public String _id;
    public String username;
}
