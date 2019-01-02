package com.forteach.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/26  11:04
 */
@EnableMongoAuditing
@EnableScheduling
@EnableMongoRepositories
@SpringBootApplication
public class WebsocketApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WebsocketApplication.class);
        app.setRegisterShutdownHook(true);
        app.run(args);
    }

}

