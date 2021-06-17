/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package com.kalsym.userservice.services;

import com.kalsym.userservice.models.email.*;
import com.kalsym.userservice.UserServiceApplication;
import com.kalsym.userservice.models.daos.Customer;
import com.kalsym.userservice.models.daos.CustomerEmailVerification;
import com.kalsym.userservice.models.daos.ClientEmailVerification;
import com.kalsym.userservice.models.daos.ClientPasswordReset;
import com.kalsym.userservice.models.daos.Client;
import com.kalsym.userservice.models.daos.PasswordReset;
import com.kalsym.userservice.repositories.ClientEmailVerificationsRepository;
import com.kalsym.userservice.repositories.ClientPasswordResetsRepository;
import com.kalsym.userservice.repositories.CustomerEmailVerificationsRepository;
import com.kalsym.userservice.utils.Logger;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Sarosh
 */
@Service
public class EmaiVerificationlHandler {

    @Autowired
    ClientEmailVerificationsRepository clientEmailVerificationsRepository;

    @Autowired
    CustomerEmailVerificationsRepository customerEmailVerificationsRepository;

    @Autowired
    ClientPasswordResetsRepository clientPasswordResetsRepository;

    @Value("${symplified.email.verification.from:no-reply@symplified.biz}")
    private String emailVerificationFrom;

    @Value("${symplified.user.service.url:https://api.symplified.biz/user-service/v1}")
    private String userServicenUrl;

    @Value("${symplified.email.service.url:http://209.58.160.20:2001}")
    private String emailServiceUrl;

    @Value("${symplified.mechant-portal.url:https://symplified.biz}")
    private String merchantPortalUrl;

    public boolean sendEmail(String[] recipients, String body) throws Exception {
        String logprefix = "sendEmail";

        RestTemplate restTemplate = new RestTemplate();

        Email email = new Email();

        email.setTo(recipients);

        AccountVerificationEmailBody aveb = new AccountVerificationEmailBody();
        aveb.setActionType(AccountVerificationEmailBody.ActionType.EMAIL_VERIFICATION);
        aveb.setLink(body);
        email.setUserAccountBody(aveb);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer accessToken");

        HttpEntity<Email> httpEntity = new HttpEntity<>(email, headers);
        String url = emailServiceUrl + "/email/no-reply/user-account";
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

    public boolean sendVerificationEmail(Object user) throws Exception {
        String logprefix = "sendVerificationEmail";

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        String email = null;
        String userId = null;

        Customer customer = null;
        Client client = null;
        try {
            customer = (Customer) user;
            email = customer.getEmail();
            userId = customer.getId();
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user is a customer", "");

        } catch (Exception e) {
            Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "cannot cast user to customer", "");

        }

        try {
            client = (Client) user;
            email = client.getEmail();
            userId = client.getId();
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user is a client", "");

        } catch (Exception e) {
            Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "cannot cast user to client", "");
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "userId: " + userId + " email: " + email, "");

        String verificationUrl = merchantPortalUrl;

        String generatedCode = generateCode();

        if (customer != null) {
            verificationUrl = verificationUrl + "/email-verified?id={{userId}}&code={{code}}";
            verificationUrl = verificationUrl.replace("{{userId}}", userId);
            verificationUrl = verificationUrl.replace("{{code}}", generatedCode);

            CustomerEmailVerification cev = new CustomerEmailVerification();

            cev.setCode(generatedCode);
            cev.setCreated(new Date());
            cev.setUpdated(new Date());
            cev.setEmail(email);
            cev.setIsVerified(Boolean.FALSE);
            cev.setCustomerId(userId);
            customerEmailVerificationsRepository.save(cev);

            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customer emailVerificationCreated: " + userId + " email: " + email, "");

        } else if (client != null) {
            verificationUrl = verificationUrl + "/email-verified?id={{userId}}&code={{code}}";
            verificationUrl = verificationUrl.replace("{{userId}}", userId);
            verificationUrl = verificationUrl.replace("{{code}}", generatedCode);

            ClientEmailVerification cev = new ClientEmailVerification();

            cev.setCode(generatedCode);
            cev.setCreated(new Date());
            cev.setUpdated(new Date());
            cev.setEmail(email);
            cev.setIsVerified(Boolean.FALSE);
            cev.setClientId(userId);
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client " + client, "");

            clientEmailVerificationsRepository.save(cev);

            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client emailVerificationCreated: " + userId + " email: " + email, "");

        }

        String[] recipients = {email};

        return sendEmail(recipients, verificationUrl);
    }

    public boolean verifyEmail(Object user, String code) {
        String logprefix = "verifyEmail";

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        String email = null;
        String userId = null;

        Customer customer = null;
        Client client = null;
        try {
            customer = (Customer) user;
            email = customer.getEmail();
            userId = customer.getId();
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user is a customer", "");

        } catch (Exception e) {
            Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "cannot cast user to customer", "");

        }

        try {
            client = (Client) user;
            email = client.getEmail();
            userId = client.getId();
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user is a client", "");

        } catch (Exception e) {
            Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "cannot cast user to client", "");
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "userId: " + userId + " email: " + email, "");

        boolean verified = false;
        if (customer != null) {
            List<CustomerEmailVerification> cevs = customerEmailVerificationsRepository.findByCustomerId(userId);

            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customer cevs: " + cevs.size(), "");

            for (CustomerEmailVerification cev : cevs) {
                if (cev.getCode().equals(code)) {
                    verified = true;
                    cev.setIsVerified(true);
                    customerEmailVerificationsRepository.save(cev);
                }
            }

            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customer verified: " + verified, "");

        } else if (client != null) {
            List<ClientEmailVerification> cevs = clientEmailVerificationsRepository.findByClientId(userId);

            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client cevs: " + cevs.size(), "");

            for (ClientEmailVerification cev : cevs) {
                if (cev.getCode().equals(code)) {
                    verified = true;
                    cev.setIsVerified(true);
                    clientEmailVerificationsRepository.save(cev);
                }
            }

            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client verified: " + verified, "");

        }

        return verified;

    }

    public boolean sendPasswordReset(Object user) throws Exception {
        String logprefix = "sendPasswordReset";

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        String email = null;
        String userId = null;

        Customer customer = null;
        Client client = null;
        try {
            customer = (Customer) user;
            email = customer.getEmail();
            userId = customer.getId();
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user is a customer", "");

        } catch (Exception e) {
            Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "cannot cast user to customer", "");

        }

        try {
            client = (Client) user;
            email = client.getEmail();
            userId = client.getId();
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user is a client", "");

        } catch (Exception e) {
            Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "cannot cast user to client", "");
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "userId: " + userId + " email: " + email, "");

        String verificationUrl = merchantPortalUrl;

        String generatedCode = generateCode();

        if (customer != null) {
            verificationUrl = verificationUrl + "/customers/" + userId + "/password/" + generatedCode + "/reset";
            CustomerEmailVerification cev = new CustomerEmailVerification();

            cev.setCode(generatedCode);
            cev.setCreated(new Date());
            cev.setUpdated(new Date());
            cev.setEmail(email);
            cev.setIsVerified(Boolean.FALSE);
            cev.setCustomerId(userId);
            customerEmailVerificationsRepository.save(cev);

            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customer password reset created: " + userId + " email: " + email, "");

        } else if (client != null) {
            verificationUrl = verificationUrl + "/clients/" + userId + "/password/" + generatedCode + "/reset";
            ClientEmailVerification cev = new ClientEmailVerification();

            cev.setCode(generatedCode);
            cev.setCreated(new Date());
            cev.setUpdated(new Date());
            cev.setEmail(email);
            cev.setIsVerified(Boolean.FALSE);
            cev.setClientId(userId);
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client " + client, "");

            clientEmailVerificationsRepository.save(cev);

            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client password reset created: " + userId + " email: " + email, "");

        }

        String[] recipients = {email};

        String subject = "Email Verification";

        String body = "Please reset your password by click below link\n" + verificationUrl;

        return sendEmail(recipients, body);
    }

    public boolean verifyPasswordReset(Object user, String code) {
        String logprefix = "verifyPasswordReset";

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "", "");

        String email = null;
        String userId = null;

        Customer customer = null;
        Client client = null;
        try {
            customer = (Customer) user;
            email = customer.getEmail();
            userId = customer.getId();
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user is a customer", "");

        } catch (Exception e) {
            Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "cannot cast user to customer", "");

        }

        try {
            client = (Client) user;
            email = client.getEmail();
            userId = client.getId();
            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "user is a client", "");

        } catch (Exception e) {
            Logger.application.warn(Logger.pattern, UserServiceApplication.VERSION, logprefix, "cannot cast user to client", "");
        }

        Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "userId: " + userId + " email: " + email, "");

        boolean verified = false;
        if (customer != null) {
            List<CustomerEmailVerification> cevs = customerEmailVerificationsRepository.findByCustomerId(userId);

            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customer cevs: " + cevs.size(), "");

            for (CustomerEmailVerification cev : cevs) {
                if (cev.getCode().equals(code)) {
                    verified = true;
                    cev.setIsVerified(true);
                    customerEmailVerificationsRepository.save(cev);
                }
            }

            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "customer verified: " + verified, "");

        } else if (client != null) {
            List<ClientPasswordReset> cprs = clientPasswordResetsRepository.findByClientId(userId);

            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client password resets: " + cprs.size(), "");

            for (ClientPasswordReset cpr : cprs) {
                if (cpr.getCode().equals(code)) {
                    verified = true;
                    cpr.setStatus(PasswordReset.PasswordResetStatus.VERIFIED);
                    clientPasswordResetsRepository.save(cpr);
                }
            }

            Logger.application.info(Logger.pattern, UserServiceApplication.VERSION, logprefix, "client code verified: " + verified, "");

        }

        return verified;

    }

    private String generateCode() {
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder((100000 + rnd.nextInt(900000)) + "-");
        for (int i = 0; i < 10; i++) {
            sb.append(chars[rnd.nextInt(chars.length)]);
        }

        return sb.toString();
    }
}
