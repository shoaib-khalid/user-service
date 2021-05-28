package com.kalsym.userservice.services;

import com.kalsym.userservice.models.storeagent.StoreAgentResponse;
import com.kalsym.userservice.models.storeagent.LiveChatResponse;
import com.kalsym.userservice.models.storeagent.LiveChatStoreAgent;
import com.kalsym.userservice.utils.Logger;
import com.kalsym.userservice.UserServiceApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author saros
 */
@Service
public class StoreAgentsHandler {

    @Value("${livechat.store.agent.creation.url:http://209.58.160.20:3000/api/v1/users.create}")
    private String livechatStoreAgentCreationUrl;

    @Value("${livechat.store.agent.deletion.url:http://209.58.160.20:3000/api/v1/users.delete}")
    private String livechatStoreAgentDeletionUrl;

    @Value("${livechat.store.agent.group.invitation.url:http://209.58.160.20:7071/stores/<storeId>/livechat/agentinvite}")
    private String livechatStoreAgentGroupInvitationUrl;

    @Value("${livechat.token:GvKS_Z_MvqDeExBPAmSrXdwXMYOlrsW3JkuSpsO9l76}")
    private String livechatToken;

    @Value("${livechat.userid:JEdxZxgW4R5Z53xq2}")
    private String livechatUserId;

    public StoreAgentResponse createAgent(LiveChatStoreAgent storeAgent) {
        String logprefix = "createAgent";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", livechatToken);
        headers.add("X-User-Id", livechatUserId);

        HttpEntity<LiveChatStoreAgent> entity;
        entity = new HttpEntity<>(storeAgent, headers);

        try {
            ResponseEntity<LiveChatResponse> res = restTemplate.exchange(livechatStoreAgentCreationUrl, HttpMethod.POST, entity, LiveChatResponse.class);

            if (res.getBody().success == true) {
                Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, " created agent " + res.getBody());

                return res.getBody().user;
            } else {
                return null;
            }
        } catch (RestClientException e) {
            Logger.application.error(Logger.pattern, UserServiceApplication.VERSION, logprefix, " could not create agent", e);

        }
        return null;
    }

    public Object deleteAgent(String id) {
        String logprefix = "createAgent";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", livechatToken);
        headers.add("X-User-Id", livechatUserId);

        class DeleteAgent {

            private String userId;

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }
        }

        DeleteAgent deleteAgent = new DeleteAgent();
        deleteAgent.setUserId(id);
        HttpEntity<DeleteAgent> entity;
        entity = new HttpEntity<>(deleteAgent, headers);

        try {
            ResponseEntity<LiveChatResponse> res = restTemplate.exchange(livechatStoreAgentDeletionUrl, HttpMethod.POST, entity, LiveChatResponse.class);

            if (res.getBody().success == true) {
                Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, " deleted agent " + res.getBody());

                return res.getBody().user;
            } else {
                return null;
            }
        } catch (RestClientException e) {
            Logger.application.error(Logger.pattern, UserServiceApplication.VERSION, logprefix, " could not delete agent", e);

        }
        return null;
    }

    public void inviteAgentToGroup(String agentId, String storeId) {
        String logprefix = "createAgent";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer accessToken");

        class LiveChatAgentInvite {

            private String userId;

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }
        }

        LiveChatAgentInvite liveChatAgentInvite = new LiveChatAgentInvite();
        liveChatAgentInvite.setUserId(agentId);
        HttpEntity<LiveChatAgentInvite> entity;
        entity = new HttpEntity<>(liveChatAgentInvite, headers);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, " agent invite entity: " + entity);

        String url = livechatStoreAgentGroupInvitationUrl.replace("<storeId>", storeId);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, " url: " + url);

        ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, " agent invite res: " + res.getBody());
    }

}
