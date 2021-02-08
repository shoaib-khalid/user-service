package com.kalsym.usersservice.models;

import com.kalsym.usersservice.models.daos.Session;
import java.util.List;

/**
 *
 * @author Sarosh
 */
public class AuthenticationReponse {

    private Session session;
    private List<String> authorities;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

}
