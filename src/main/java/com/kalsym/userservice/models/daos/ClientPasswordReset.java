package com.kalsym.userservice.models.daos;

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
@Table(name = "client_password_reset")
public class ClientPasswordReset extends PasswordReset implements Serializable {

    private String clientId;
}
