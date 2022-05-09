package com.kalsym.userservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.kalsym.userservice.UserServiceApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import org.json.JSONObject;
import java.util.Date;
import java.sql.Time;

import com.kalsym.userservice.utils.Logger;

/**
 *
 * @author 7cu
 */
@Service
/**
 * Used to post the order in live.symplifed (rocket chat)
 */
public class OrderService {

    //@Autowired
    @Value("${orderService.claimnewuservoucher.URL:not-set}")
    String orderServiceClaimNewUserVoucherURL;
   
    public OrderServiceResponse claimNewUserVoucher(String customerId) {
        String logprefix = "createCenterCode";

        try {

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer accessToken");
            
            HttpEntity httpEntity = new HttpEntity(null, headers);
            
            String url = orderServiceClaimNewUserVoucherURL.replace("<customerId>", customerId);
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Calling order service url : "+url);
            ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Request sent to order service, responseCode: " + res.getStatusCode() + ", responseBody: " + res.getBody());

            if (res.getStatusCode() == HttpStatus.OK) {
                Gson gson = new Gson();
                OrderServiceResponse response = gson.fromJson(res.getBody(), OrderServiceResponse.class);
                Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "OrderServiceResponse:" + response.toString());
                return response;
            }
        } catch (RestClientException e) {
            Logger.application.error(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Error claim newuser voucher: " + orderServiceClaimNewUserVoucherURL, e);
            return null;
        } catch (Exception e) {
            Logger.application.error(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Error claim newuser voucher: " + orderServiceClaimNewUserVoucherURL, e);
            return null;
        }
        return null;
    }


}
