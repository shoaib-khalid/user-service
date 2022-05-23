/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kalsym.userservice.models.email;

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
public class AccountVerificationEmailBody {

    public enum ActionType {
        EMAIL_VERIFICATION("Email Verification"),
        ACCOUNT_CREATED_NOTIFICATION("Congratulation! Your DeliverIn account is created."),
        PASSWORD_RESET("Password Reset");

        public final String label;

        private ActionType(String label) {
            this.label = label;
        }
    }

    private ActionType actionType;

    private String link;


}
