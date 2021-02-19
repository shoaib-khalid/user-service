package com.kalsym.usersservice.models;

import java.util.List;

/**
 *
 * @author Sarosh
 */
public class AuthenticationReponse {

    private Object session;
    private List<String> authorities;

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

}
