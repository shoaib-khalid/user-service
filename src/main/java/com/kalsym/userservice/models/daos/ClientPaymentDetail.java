package com.kalsym.userservice.models.daos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author Sarosh
 */
@Entity
@Table(name = "client_payment_detail")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class ClientPaymentDetail implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    private String taxNumber;

    private String gstRate;

    private String stRate;

    private String whtRate;

    private String bankName;

    private String bankAccountNumber;

    private String bankAccountTitle;

    private String docImage;

    private String clientId;
    
    private String ansurMerchantId;
    
    private String ansurApiKey;

    @CreationTimestamp
    Date created;

    @UpdateTimestamp
    Date updated;

    public void update(ClientPaymentDetail cpd) {

        if (null != cpd.getBankAccountTitle()) {
            this.bankAccountTitle = cpd.getBankAccountTitle();
        }

        if (null != cpd.getBankName()) {
            this.bankName = cpd.getBankName();
        }

        if (null != cpd.getBankAccountNumber()) {
            this.bankAccountNumber = cpd.getBankAccountNumber();
        }

        if (null != cpd.getTaxNumber()) {
            this.taxNumber = cpd.getTaxNumber();
        }

        if (null != cpd.getStRate()) {
            this.stRate = cpd.getStRate();
        }

        if (null != cpd.getWhtRate()) {
            this.whtRate = cpd.getWhtRate();
        }
        
        if (null != cpd.getAnsurMerchantId()) {
            this.ansurMerchantId = cpd.getAnsurMerchantId();
        }
        
        if (null != cpd.getAnsurApiKey()) {
            this.ansurApiKey = cpd.getAnsurApiKey();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ClientPaymentDetail other = (ClientPaymentDetail) obj;
        return Objects.equals(this.id, other.getId());
    }

}
