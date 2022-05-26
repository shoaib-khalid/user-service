/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package com.kalsym.userservice.models;

/**
 *
 * @author taufik
 */
public enum Error {
    
    //ERROR
    RECORD_NOT_FOUND("101"),
    CONNECTION_ERROR("102"),
    
    
    //SUCCESS
    RECORD_CREATED("201"),
    ;    
     
    public final String errorCode;
        
    private Error(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getCode() {
        return this.errorCode;
    }
}
