package com.kalsym.userservice.models.daos;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author 7cu
 */
@Entity
@Getter
@Setter
@Table(name = "app_token")
@ToString
public class AppToken implements Serializable {

    @Id
    private String appId;

    private String userId;

    private String token;

    @CreationTimestamp
    private Date created;

    public void update(AppToken apptoken) {

        if (null != apptoken.getAppId()) {
            appId = apptoken.getAppId();
        }
        if (null != apptoken.getUserId()) {
            userId = apptoken.getUserId();
        }

        if (null != apptoken.getToken()) {
            token = apptoken.getToken();
        }

    }
}
