/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package com.kalsym.userservice.models.storeagent;

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
public class LiveChatGroupInvite {
    private String roomId;
    private String userId;
}
