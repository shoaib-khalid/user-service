package com.kalsym.userservice.models.daos;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

/**
 *
 * @author 7cu
 */
@Entity
@Getter
@Setter
@Table(name = "error_code")
@ToString
public class ErrorCode implements Serializable {

    @Id
    private String errorCode;

    private String errorDescription;

    private String errorMessage;

    @CreationTimestamp
    private Date created;

   
}
