package com.kalsym.userservice.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.kalsym.userservice.models.daos.ErrorCode;
import com.kalsym.userservice.repositories.ErrorCodeRepository;
import com.kalsym.userservice.utils.DateTimeUtil;
import java.util.Date;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 *
 * @author Sarosh
 */
@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class HttpReponse {

    public HttpReponse(String requestUri) {
        this.timestamp = DateTimeUtil.currentTimestamp();
        this.path = requestUri;
    }

    private Date timestamp;
    private int status;
    private String error;
    private String message;
    private Object data;
    private String path;
    private String errorCode;

    /**
     * *
     * Sets success and message as reason phrase of provided status.
     *
     * @param status
     */
    public void setStatus(HttpStatus status) {
        this.status = status.value();
        this.message = status.getReasonPhrase();
    }

    /**
     * *
     * Sets status and custom message.
     *
     * @param status
     * @param message
     */
    public void setStatus(HttpStatus status, String message) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
    }
    
   
    /**
     * *
     * Sets status and custom message.
     *
     * @param status
     * @param error
     * @param errorCodeRepository
     */
    public void setStatus(HttpStatus status, Error error, ErrorCodeRepository errorCodeRepository) {
        Optional<ErrorCode> errorCodeOpt = errorCodeRepository.findById(error.errorCode);
        if (errorCodeOpt.isPresent()) {
            this.message = errorCodeOpt.get().getErrorMessage();
            this.errorCode = errorCodeOpt.get().getErrorCode();
        }
    }

}
