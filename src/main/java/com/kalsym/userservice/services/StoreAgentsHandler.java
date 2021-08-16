package com.kalsym.userservice.services;

import com.kalsym.userservice.models.storeagent.StoreAgentResponse;
import com.kalsym.userservice.models.storeagent.LiveChatResponse;
import com.kalsym.userservice.models.storeagent.LiveChatStoreAgent;
import com.kalsym.userservice.utils.Logger;
import com.kalsym.userservice.UserServiceApplication;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.Conflict;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author saros
 */
@Service
public class StoreAgentsHandler {

    @Value("${livechat.store.agent.creation.url:https://live.symplified.biz/api/v1/users.create}")
    private String livechatStoreAgentCreationUrl;

    @Value("${livechat.store.agent.deletion.url:https://live.symplified.biz/api/v1/users.delete}")
    private String livechatStoreAgentDeletionUrl;

    @Value("${livechat.store.order.agent.group.invitation.url:https://api.symplified.biz/product-service/v1/stores/<storeId>/livechat/order-csr/agentinvite}")
    private String livechatStoreOrdersAgentGroupInvitationUrl;

    @Value("${livechat.store.complaint.agent.group.invitation.url:https://api.symplified.biz/product-service/v1/stores/<storeId>/livechat/complaint-csr/agentinvite}")
    private String livechatStoreComplaintAgentGroupInvitationUrl;

    //@Value("${livechat.token:GvKS_Z_MvqDeExBPAmSrXdwXMYOlrsW3JkuSpsO9l76}")
    private String liveChatToken;

    @Value("${livechat.userid:JEdxZxgW4R5Z53xq2}")
    private String liveChatUserId;

    @Value("${liveChatlogin.username:order}")
    private String liveChatLoginUsername;

    @Value("${liveChat.login.password:sarosh@1234}")
    private String liveChatLoginPassword;

    @Value("${liveChat.login.url:https://api.symplified.biz/api/v1/login}")
    private String liveChatLoginUrl;

    public LiveChatResponse createAgent(LiveChatStoreAgent storeAgent) throws RestClientException, HttpClientErrorException {
        String logprefix = "createAgent";

        if (!loginLiveChat()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "live chat not logged in");
            return null;
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", liveChatToken);
        headers.add("X-User-Id", liveChatUserId);

        HttpEntity<LiveChatStoreAgent> entity;
        entity = new HttpEntity<>(storeAgent, headers);

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "livechatStoreAgentCreationUrl: " + livechatStoreAgentCreationUrl);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "entity: " + entity);
        ResponseEntity<LiveChatResponse> res = null;
        try {
            res = restTemplate.exchange(livechatStoreAgentCreationUrl, HttpMethod.POST, entity, LiveChatResponse.class);
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, " created agent " + res.getBody());

        } catch (RestClientException e) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "message from RC " + e.getMessage());
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "message from RC2:  " + e.getMessage().replace("400 Bad Request: ", ""));

            JSONArray ar = new JSONArray(e.getMessage().replace("400 Bad Request: ", ""));
            JSONObject obj = ar.getJSONObject(0);
            LiveChatResponse lcr = new LiveChatResponse();

            if (obj.getString("error").contains("already in use")) {
                lcr.setError("Username already exists in Live Chat");
            } else {
                lcr.setError("User could not be created at Live Chat");
            }

            lcr.setSuccess(false);
            return lcr;
        }

        return res.getBody();
    }

    public Object deleteAgent(String id) {
        String logprefix = "deleteAgent";

        if (!loginLiveChat()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "live chat not logged in");
            return null;
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", liveChatToken);
        headers.add("X-User-Id", liveChatUserId);

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
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "agent: " + deleteAgent);

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

    public void inviteComplaintCsrAgentToGroup(String agentId, String storeId) {
        String logprefix = "createAgent";

        if (!loginLiveChat()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "live chat not logged in");
            return;
        }

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

        String url = livechatStoreComplaintAgentGroupInvitationUrl.replace("<storeId>", storeId);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, " url: " + url);

        ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, " agent invite res: " + res.getBody());
    }

    public void inviteOrderAgentToGroup(String agentId, String storeId) {
        String logprefix = "createAgent";

        if (!loginLiveChat()) {
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "live chat not logged in");
            return;
        }

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

        String url = livechatStoreOrdersAgentGroupInvitationUrl.replace("<storeId>", storeId);
        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, " url: " + url);

        ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, " agent invite res: " + res.getBody());
    }

    public boolean loginLiveChat() {
        String logprefix = "loginLiveChat";
        if (null != liveChatToken) {
            return true;
        }
        class LoginRequest {

            public String user;
            public String password;

            public LoginRequest() {
            }

            public LoginRequest(String user, String password, String code) {
                this.user = user;
                this.password = password;
            }

            public String getUser() {
                return user;
            }

            public void setUser(String user) {
                this.user = user;
            }

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            @Override
            public String toString() {
                return "LoginRequest{" + "user=" + user + ", password=" + password + '}';
            }

        }

        RestTemplate restTemplate = new RestTemplate();

        LoginRequest loginRequest = new LoginRequest();

        loginRequest.setUser(liveChatLoginUsername);
        loginRequest.setPassword(liveChatLoginPassword);

        HttpEntity<LoginRequest> httpEntity = new HttpEntity<>(loginRequest);

        try {

            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "liveChatLoginUrl: " + liveChatLoginUrl);

            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "httpEntity: " + httpEntity);

            ResponseEntity<LiveChatLoginReponse> res = restTemplate.exchange(liveChatLoginUrl, HttpMethod.POST, httpEntity, LiveChatLoginReponse.class);
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "res: " + res);

            LiveChatLoginReponse liveChatLoginReponse = res.getBody();

            liveChatUserId = liveChatLoginReponse.getData().userId;
            liveChatToken = liveChatLoginReponse.getData().authToken;
            return true;
        } catch (Exception e) {
            Logger.application.error(Logger.pattern, UserServiceApplication.VERSION, logprefix, "Error loging in livechat ", e);
            return false;
        }
    }

}
