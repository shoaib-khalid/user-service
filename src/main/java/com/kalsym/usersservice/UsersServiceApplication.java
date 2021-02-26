package com.kalsym.usersservice;

import com.kalsym.usersservice.utils.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class UsersServiceApplication {

    public static String VERSION;

    public static String NAME;

    public static String DESCRIPTION;

    public static void main(String[] args) {
        SpringApplication.run(UsersServiceApplication.class, args);
    }

    @Value("${build.version:not-known}")
    String version;

    @Value("${build.name:not-known}")
    String name;

    @Value("${build.desciption:not-known}")
    String description;

    @Bean
    CommandLineRunner lookup(ApplicationContext context) {
        return args -> {
            UsersServiceApplication.VERSION = version;
            VERSION = version;
            NAME = name;
            DESCRIPTION = description;

            Logger.application.info("[v{}][{}] {}", UsersServiceApplication.VERSION, "", "\n"
                    + "                                                 _          \n"
                    + "                                                (_)         \n"
                    + "  _   _ ___  ___ _ __ ______ ___  ___ _ ____   ___  ___ ___ \n"
                    + " | | | / __|/ _ \\ '__|______/ __|/ _ \\ '__\\ \\ / / |/ __/ _ \\\n"
                    + " | |_| \\__ \\  __/ |         \\__ \\  __/ |   \\ V /| | (_|  __/\n"
                    + "  \\__,_|___/\\___|_|         |___/\\___|_|    \\_/ |_|\\___\\___|\n"
                    + "                                                            \n"
                    + " :: com.kalsym ::              (v" + UsersServiceApplication.VERSION + ")");
        };
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
