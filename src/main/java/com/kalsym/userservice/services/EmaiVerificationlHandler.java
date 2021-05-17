/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package com.kalsym.userservice.services;

import com.kalsym.userservice.UserServiceApplication;
import com.kalsym.userservice.models.HttpReponse;
import com.kalsym.userservice.models.daos.Administrator;
import com.kalsym.userservice.models.daos.Customer;
import com.kalsym.userservice.models.daos.CustomerEmailVerification;
import com.kalsym.userservice.models.daos.ClientEmailVerification;
import com.kalsym.userservice.models.daos.Client;
import com.kalsym.userservice.repositories.ClientEmailVerificationsRepository;
import com.kalsym.userservice.repositories.ClientSessionsRepository;
import com.kalsym.userservice.repositories.ClientsRepository;
import com.kalsym.userservice.repositories.CustomerEmailVerificationsRepository;
import com.kalsym.userservice.repositories.CustomerSessionsRepository;
import com.kalsym.userservice.repositories.CustomersRepository;
import com.kalsym.userservice.utils.Logger;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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

//    @Autowired
//    CustomersRepository customersRepository;
//
//    @Autowired
//    ClientsRepository clientsRepository;
    @Value("${symplified.email.verification.from:no-reply@symplified.biz}")
    private String emailVerificationFrom;

    @Value("${symplified.email.verification.url:http://209.58.160.20:20921}")
    private String emailVerificationUrl;

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String[] recipients, String from, String subject, String body) {

        SimpleMailMessage msg = new SimpleMailMessage();
        //msg.setTo("to_1@gmail.com", "to_2@gmail.com", "to_3@yahoo.com");
        msg.setFrom(from);
        msg.setTo(recipients);

        msg.setSubject(subject);
        msg.setText(body);

        javaMailSender.send(msg);

    }

    public void sendVerificationEmail(Object user) {
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

        String verificationUrl = emailVerificationUrl;

        String generatedCode = generateCode();

        if (customer != null) {
            verificationUrl = verificationUrl + "/customers/" + userId + "/email-verification/" + generatedCode + "/verify";
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
            verificationUrl = verificationUrl + "/clients/" + userId + "/email-verification/" + generatedCode + "/verify";
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

        String subject = "Verify Email";

        String body = "Please verify your email by click below link\n" + verificationUrl;

        sendEmail(recipients, emailVerificationFrom, subject, body);

    }

    public boolean verify(Object user, String code) {
        String logprefix = "verify";

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
