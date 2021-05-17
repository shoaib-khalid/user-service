package com.kalsym.userservice.models.daos;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author Sarosh
 */
@Entity
@Table(name = "user_channel")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class UserChannel implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    private Date created;
    private Date updated;

    private String refId;
    private String userId;

    private String channelName;
    
    private String token;

    public void update(UserChannel userChannel) {
        if (null != userChannel.getId()) {
            id = userChannel.getId();
        }

        if (null != userChannel.getRefId()) {
            this.refId = userChannel.getRefId();
        }

        if (null != userChannel.getChannelName()) {
            this.channelName = userChannel.getChannelName();
        }
        
        if (null != userChannel.getToken()) {
            this.token = userChannel.getToken();
        }

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.id);
        hash = 37 * hash + Objects.hashCode(this.refId);
        hash = 37 * hash + Objects.hashCode(this.userId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserChannel other = (UserChannel) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.refId, other.refId)) {
            return false;
        }
        if (!Objects.equals(this.userId, other.userId)) {
            return false;
        }
        return true;
    }

}
