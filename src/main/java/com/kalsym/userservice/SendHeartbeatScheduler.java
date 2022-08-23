/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kalsym.userservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kalsym.userservice.repositories.ClientsRepository;
import com.kalsym.userservice.repositories.StoreRepository;
import com.kalsym.userservice.utils.Logger;
import com.kalsym.userservice.utils.DateTimeUtil;
import com.kalsym.userservice.services.FCMService;
import com.kalsym.userservice.models.Store;

import java.util.List;

/**
 *
 * @author taufik
 */

@Component
public class SendHeartbeatScheduler {
    
    @Autowired
    ClientsRepository clientRepository;
    
    @Autowired
    StoreRepository storeRepository;
    
    @Autowired
    FCMService fcmService;
           
    @Value("${mobileapp.heartbeat.scheduler.enabled:false}")
    private boolean isEnabled;
    
    @Value("${mobileapp.heartbeat.scheduler.sleep:1}")
    private int sleepInMinutes;
   
    @Scheduled(fixedDelay = 600000)
    public void sendHeartbeat() throws Exception {
        if (isEnabled) {
            String logprefix = "SendHeartbeatScheduler";            
            String[] suffixList = {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
            
            for (int x=0;x<suffixList.length;x++) {
                String suffix = suffixList[x];
                List<Object[]> userList = clientRepository.getActiveClient(suffix);
                Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Query active client for suffix:"+suffix+" Total Found:"+userList.size());
                
                for (int i=0;i<userList.size();i++) {
                    Object[] data = userList.get(i);
                    String clientId = (String)data[0];
                    
                    //get storeId
                    List<Store> storeList = storeRepository.findByClientId(clientId);
                    if (storeList.size()>0) {
                        //generate transactionId
                        String transactionId = DateTimeUtil.currentTimestampString()+"-"+clientId;
                        Store store = storeList.get(0);
                        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Sending FCM to clientId:"+clientId+" storeId:"+store.getId());                    
                        fcmService.sendPushNotification(clientId, store.getId(), transactionId, store.getDomain());
                        clientRepository.UpdatePingTransactionId(clientId, transactionId);
                    }
               
                }
                
                Thread.sleep(sleepInMinutes * 60 * 1000);
                Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Sleep for "+sleepInMinutes);                    
            }
        }   
    }
    
    
}

