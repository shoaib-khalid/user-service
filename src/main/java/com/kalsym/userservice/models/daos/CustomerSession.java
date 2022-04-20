package com.kalsym.userservice.models.daos;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author Sarosh
 */
@Entity
@Getter
@Setter
@Table(name = "customer_session")
public class CustomerSession extends Session implements Serializable {

   private String domain;

}
