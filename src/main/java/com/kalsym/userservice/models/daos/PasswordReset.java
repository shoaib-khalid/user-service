package com.kalsym.userservice.models.daos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author Sarosh
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@MappedSuperclass
@ToString
public class PasswordReset {

    public enum PasswordResetStatus {
        VERIFIED,
        EXPIRED
    };

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String code;
    private String email;
    private PasswordResetStatus status;

    @CreationTimestamp
    private Date created;

    @UpdateTimestamp
    private Date updated;

    public void verify() throws Exception {
        this.status = PasswordResetStatus.VERIFIED;
    }

}
