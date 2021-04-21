package com.kalsym.usersservice.models.daos;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Sarosh
 */
@Entity
@Getter
@Setter
@Table(name = "client_email_verification")
public class ClientEmailVerification extends EmailVerification implements Serializable {

    private String clientId;
}
