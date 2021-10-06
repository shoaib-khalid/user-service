package com.kalsym.userservice.models;

import java.util.List;

/**
 *
 * @author Sarosh
 */
public class Auth {

    private Object session;

    private String role;
    private List<String> authorities;
    private String sessionType;
    
    

    public Object getSession() {
        return session;
    }

    public void setSession(Object session) {
        this.session = session;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
     public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

}
