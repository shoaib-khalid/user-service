package com.kalsym.userservice.models.daos;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author Sarosh
 */
@Entity
@Getter
@Setter
@Table(name = "guest_session")
public class GuestSession extends Session implements Serializable {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    
    private String ip;
    private String os;
    private String device;
    
    @CreationTimestamp
    Date created;

    @UpdateTimestamp
    Date updated;
  

}
