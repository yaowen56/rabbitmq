package com.example.rabbitmq.mq;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author calvin
 * @date 2020/10/12 15:15
 */
@Component
@RabbitListener(queues = "TestDirectQueue")//监听的队列名称 TestDirectQueue
public class DirectReceiverNew {
    @RabbitHandler
    public void process(Map testMessage) {
        System.out.println("我是新的--DirectReceiver消费者收到消息  : " + testMessage.toString());
    }
}
