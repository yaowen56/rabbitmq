package com.example.rabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ResourceUtils;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@SpringBootApplication
public class RabbitmqApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitmqApplication.class, args);
        System.out.println("\n------------------------- 项目启动成功 -----------------------\n");
        try {
            // String simheiPath = ResourceUtils.getFile("classpath:simhei.ttf").getPath();

            ITextRenderer render = new ITextRenderer();
            ITextFontResolver fontResolver = render.getFontResolver();
            fontResolver.addFont("simhei.ttf", "Identity-H", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
