package com.kalsym.userservice.models.daos;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author Sarosh
 */
@Entity
@Getter
@Setter
@Table(name = "administrator_session")
public class AdministratorSession extends Session implements Serializable {

   

}
