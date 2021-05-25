package com.kalsym.userservice.models.daos;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

    public void update(CustomerAddress customerAddress) {

    }

}