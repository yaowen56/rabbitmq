package com.example.rabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;

@SpringBootApplication
public class RabbitmqApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitmqApplication.class, args);
        System.out.println("\n------------------------- 项目启动成功 -----------------------\n");
        try {
            String simheiPath = ResourceUtils.getFile("classpath:simhei.ttf").getPath();
            System.out.println(simheiPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
