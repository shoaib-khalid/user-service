package com.kalsym.userservice.models.daos;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
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
@Table(name = "tag_config")
public class TagConfig implements Serializable {
    @Id
    private Integer id;
    
    private Integer tagId;
    
    private String property;
    
    private String content;
            

}
