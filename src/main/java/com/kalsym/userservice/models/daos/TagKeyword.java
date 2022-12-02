package com.kalsym.userservice.models.daos;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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
@Table(name = "tag_keyword")
public class TagKeyword implements Serializable {
    @Id
    private Integer id;
    
    private String keyword;
            
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "tagId", insertable = false, updatable = false, nullable = true)
    private List<TagConfig> tagConfigList;
}
