package com.kalsym.userservice.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Sarosh
 */
public class DateTimeUtil {

    /**
     * *
     * Generate current timestamp string with format 'yyyy-MM-dd HH:mm:ss'
     *
     * @return
     */
    public static Date currentTimestamp() {
        Date currentDate = Date.from(Instant.now());
        return currentDate;
    }

    /**
     * *
     * Generate expiry time by adding seconds
     *
     * @param seconds
     * @return
     */
    public static Date expiryTimestamp(int seconds) {
        Date expiryDate = Date.from(Instant.now().plusSeconds(seconds));
        return expiryDate;
    }
}
