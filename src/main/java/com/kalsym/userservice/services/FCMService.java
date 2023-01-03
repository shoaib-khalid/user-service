package com.kalsym.userservice.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.kalsym.userservice.UserServiceApplication;
import com.kalsym.userservice.utils.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author saros
 */
@Service
public class FCMService {

    @Value("${fcm.url:https://fcm.googleapis.com/fcm/send}")
    String fcmUrl;

    @Value("${fcm.token.deliverin:key=AAAAj5hNRLI:APA91bEBW0gxueP0sjTtvixEb41IK7mZvDxyiSMDalS6ombzXoidlwGmvsagaF520jTxZxxLd1qsX4H-8iSs2qsgqY-rpdLvpTJFOYq0EGj7Mssjno0A7Xwd7nV8pt29HmewypxfaQ65}")
    String fcmTokenDeliverIn;
    
    @Value("${fcm.token.easydukan:key=AAAAj5hNRLI:APA91bEBW0gxueP0sjTtvixEb41IK7mZvDxyiSMDalS6ombzXoidlwGmvsagaF520jTxZxxLd1qsX4H-8iSs2qsgqY-rpdLvpTJFOYq0EGj7Mssjno0A7Xwd7nV8pt29HmewypxfaQ65}")
    String fcmTokenEasyDukan;

    public void sendPushNotification(String clientId, String storeId, String transactionId, String domain) {
        String logprefix = "sendPushNotification";
        RestTemplate restTemplate = new RestTemplate();
        FCMNotification fcmNotification = new FCMNotification();
        fcmNotification.setTo("/topics/" + storeId);
        fcmNotification.setPriority("high");
        FCMNotificationData fcmNotificationData = new FCMNotificationData();
        fcmNotificationData.setTitle("heartbeat");
        fcmNotificationData.setStoreName("");
        fcmNotificationData.setBody(transactionId);
        fcmNotification.setData(fcmNotificationData);
        
        String fcmToken = fcmTokenDeliverIn;
        if (domain.contains("deliverin")) {
            fcmToken = fcmTokenDeliverIn;
        } else if (domain.contains("easydukan")) {
            fcmToken = fcmTokenEasyDukan;
        } else if (domain.contains("dev-my")) {
            fcmToken = fcmTokenDeliverIn;
        } else if (domain.contains("dev-pk")) {
            fcmToken = fcmTokenEasyDukan;
        } else {
            fcmToken = fcmTokenDeliverIn;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", fcmToken);

        HttpEntity<FCMNotification> entity = new HttpEntity<>(fcmNotification, headers);

        String url = fcmUrl;
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, " url: " + url);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, " entity: " + entity);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Sending FCM to clientId:"+clientId);
        
        ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, " res: " + res);

    }
    
    
    public void sendLogoutNotification(String staffId, String storeId, String domain) {
        String logprefix = "sendLogoutNotification";
        RestTemplate restTemplate = new RestTemplate();
        FCMNotification fcmNotification = new FCMNotification();
        fcmNotification.setTo("/topics/" + storeId);
        fcmNotification.setPriority("low");
        FCMNotificationData fcmNotificationData = new FCMNotificationData();
        fcmNotificationData.setTitle("endshift");
        fcmNotificationData.setStoreName("");
        fcmNotificationData.setBody(staffId);
        fcmNotification.setData(fcmNotificationData);
        
        String fcmToken = fcmTokenDeliverIn;
        if (domain.contains("deliverin")) {
            fcmToken = fcmTokenDeliverIn;
        } else if (domain.contains("easydukan")) {
            fcmToken = fcmTokenEasyDukan;
        } else if (domain.contains("dev-my")) {
            fcmToken = fcmTokenDeliverIn;
        } else if (domain.contains("dev-pk")) {
            fcmToken = fcmTokenEasyDukan;
        } else {
            fcmToken = fcmTokenDeliverIn;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", fcmToken);

        HttpEntity<FCMNotification> entity = new HttpEntity<>(fcmNotification, headers);

        String url = fcmUrl;
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, " url: " + url);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, " entity: " + entity);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Sending FCM to clientId:"+staffId);
        
        ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, " res: " + res);

    }

}
