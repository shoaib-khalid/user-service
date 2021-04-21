/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package com.kalsym.usersservice.models.storeagent;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author saros
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class CustomFields {

    private String storeId;
}
