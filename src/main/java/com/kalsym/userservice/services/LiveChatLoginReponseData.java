/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package com.kalsym.userservice.services;

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
public class LiveChatLoginReponseData {
    public String authToken;
    public String userId;
}
