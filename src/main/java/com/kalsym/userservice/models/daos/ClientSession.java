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
@Table(name = "client_session")
public class ClientSession extends Session implements Serializable {


}
