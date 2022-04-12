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
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author Sarosh
 */
@Entity
@Table(name = "customer_address")
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class CustomerAddress implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    private String name;

    private String address;

    private String email;

    private String phoneNumber;

    private String postCode;

    private String city;

    private String state;

    private String country;

    private String customerId;
    
    private Boolean isDefault=false;

    public void update(CustomerAddress customerAddress) {
        if (null != customerAddress.getName()) {
            name = customerAddress.getName();
        }

        if (null != customerAddress.getEmail()) {
            email = customerAddress.getEmail();
        }

        if (null != customerAddress.getCountry()) {
            country = customerAddress.getCountry();
        }

        if (null != customerAddress.getState()) {
            state = customerAddress.getState();
        }

        if (null != customerAddress.getCity()) {
            city = customerAddress.getCity();
        }

        if (null != customerAddress.getCountry()) {
            phoneNumber = customerAddress.getPhoneNumber();
        }

        if (null != customerAddress.getPostCode()) {
            postCode = customerAddress.getPostCode();
        }

        if (null != customerAddress.getAddress()) {
            address = customerAddress.getAddress();
        }
    }

}
