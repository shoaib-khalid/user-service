package com.kalsym.usersservice.models.daos;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import static java.lang.Math.random;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Date;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.tomcat.util.codec.binary.Base64;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author Sarosh
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@MappedSuperclass
public class Session {

    private String username;
    private String remoteAddress;
    private String status;
    private Date expiry;
    private String created;
    private String updated;

    private String accessToken;
    private String refreshToken;

    public void generateTokens() throws Exception {
        String accessTokenKey = UUID.randomUUID().toString();
        String refreshTokenKey = UUID.randomUUID().toString();

        String accessUuid = UUID.randomUUID().toString();

        String refreshUuid = UUID.randomUUID().toString();

        this.accessToken = Base64.encodeBase64String(token(accessTokenKey, accessUuid).getBytes());

        this.refreshToken = Base64.encodeBase64String(token(refreshTokenKey, refreshUuid).getBytes());

    }

    private String token(String keyStr, String value) throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[32];
        random.nextBytes(salt);

        KeySpec spec = new PBEKeySpec(keyStr.toCharArray(), salt, 65536, 256); // AES-256
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] key = f.generateSecret(spec).getEncoded();
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

        byte[] ivBytes = new byte[16];
        random.nextBytes(ivBytes);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, keySpec, iv);
        byte[] encValue = c.doFinal(value.getBytes());

        byte[] finalCiphertext = new byte[encValue.length + 2 * 16];
        System.arraycopy(ivBytes, 0, finalCiphertext, 0, 16);
        System.arraycopy(salt, 0, finalCiphertext, 16, 16);
        System.arraycopy(encValue, 0, finalCiphertext, 32, encValue.length);

        return finalCiphertext.toString();
    }

}
