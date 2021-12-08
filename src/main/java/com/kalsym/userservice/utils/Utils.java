/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package com.kalsym.userservice.utils;

import com.kalsym.userservice.UserServiceApplication;
import com.kalsym.userservice.models.WhatsappMessage;
import com.kalsym.userservice.models.Template;
import java.util.Random;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author taufik
 */
public class Utils {
    
     public static String generateCode() {
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder((100000 + rnd.nextInt(900000)) + "-");
        for (int i = 0; i < 10; i++) {
            sb.append(chars[rnd.nextInt(chars.length)]);
        }

        return sb.toString();
    }
     
     
     public static boolean sendWhatsappMessage(String url, String[] recipients, String[] body) throws Exception {
        String logprefix = "sendWhatsappMessage";
        RestTemplate restTemplate = new RestTemplate();        
        HttpHeaders headers = new HttpHeaders();
        WhatsappMessage request = new WhatsappMessage();
        request.setGuest(false);
        request.setRecipientIds(recipients);
        request.setRefId(recipients[0]);
        request.setReferenceId("60133429331");
        Template template = new Template();
        template.setName("welcome_to_symplified_7");
        template.setParameters(body);
        request.setTemplate(template);
        HttpEntity<WhatsappMessage> httpEntity = new HttpEntity<>(request, headers);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "url: " + url, "");
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "httpEntity: " + httpEntity, "");

        ResponseEntity<String> res = restTemplate.postForEntity(url, httpEntity, String.class);

        if (res.getStatusCode() == HttpStatus.ACCEPTED || res.getStatusCode() == HttpStatus.OK) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "res: " + res.getBody(), "");
            return true;
        } else {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "could not send verification email res: " + res, "");
            return false;
        }

    }
}
