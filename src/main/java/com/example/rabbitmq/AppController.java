package com.example.rabbitmq;

import com.example.rabbitmq.redis.RedisUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author calvin
 * @date 2020/10/12 11:08
 */
@RestController
@RequestMapping("/hello")
public class AppController {

    @Autowired
    private RabbitTemplate rabbitTemplate;  //使用RabbitTemplate,这提供了接收/发送等等方法
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisUtils redisUtils;

    @GetMapping("/hello")
    public String HelloWorld() {
        return "Hello World YaoWen 合格 nice  ```````````````````````````````!";
    }

    @GetMapping("/sendDirectMessage")
    public String sendDirectMessage() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "test message, hello!";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Map<String, Object> map = new HashMap<>();
        map.put("messageId", messageId);
        map.put("messageData", messageData);
        map.put("createTime", createTime);

        //将消息携带绑定键值：TestDirectRouting 发送到交换机TestDirectExchange
        rabbitTemplate.convertAndSend("TestDirectExchange", "TestDirectRouting", map);
        return "ok";
    }

    @GetMapping("/setSessionValue")
    public String setredisResult(HttpServletRequest request) {
        request.getSession().setAttribute(request.getSession().getId(), "---测试数据---" + request.getRequestURL());

        System.out.println(request.getSession().getId());
        return "set成功，已经存入session域且redis里面也会有值";
    }

    @GetMapping("/getSessionValue")
    public String redisResult(HttpServletRequest request) {
        System.out.println(request.getSession().getId());
        String value = String.valueOf(request.getSession().getAttribute(request.getSession().getId()));
        redisTemplate.opsForSet().add("chenyaowne", "hello");
        return "取值成功         :" + value;
    }

    @GetMapping("/userLogin")
    public String setRedisResult(HttpServletRequest request) {
        //1. 取出当期客户端的sessionId
        String sId = request.getSession().getId();
        //2. 查询该sessionId 是否存在于redis
        boolean exists = redisUtils.exists(sId);

        if (!exists) {
            //2.1未登录过,进行用户信息的校验
            //如果通过后,写入session域进行共享,即使是负载不同端口,sessionId不会发生变化
            request.getSession().setAttribute(sId, "login success");
            redisUtils.setWithTime(sId, "login success", 1000);
            return "success login!";

            //如果不通过,那么返回登录页面,省略
        } else {
            //2.2 已经登录过,则存入redis中刷新过期时间,再直接返回成功页面
            redisUtils.setWithTime(sId, "login success", 1000);
            return " yes,you are allow!";
        }

    }

    @GetMapping("/userLoginOut")
    public String userLoginOut(HttpServletRequest request) {

        String sId = request.getSession().getId();
        redisUtils.remove(sId);
        return "login out!";
    }

    //登录接口
    @GetMapping("/userLoginC")
    public String setRedisResult(HttpServletRequest request, HttpServletResponse response){

        //第一次登录

        //1. 取出当期客户端的sessionId
        String sId=request.getSession().getId();

        String cookies = getCookies(request);

        if (cookies==null || cookies.equals("")){

            System.out.println("没有登录过,准备进行登录！");
            //执行登录逻辑

            //写入cookie
            writeLoginToken(response,sId); //这里设置cookie的过期时间应当与redis设置的时间且与session失效时间保持一致
            //写入redis
            redisUtils.setWithTime(sId,"userInfo",1000);
            System.out.println("登录成功！");
            return "success login!";
        }else{

            boolean exists = redisUtils.exists(cookies);
            if (exists){

                System.out.println("已经登录过,正常登录！");
                return " yes,you are allow!";
            }else {

                return "信息异常不允许登录";
            }
        }



    }


    /**
     * 获取浏览器存入
     * @param request
     * @return
     */
    public  String getCookies(HttpServletRequest request){
        //HttpServletRequest 装请求信息类
        //HttpServletRespionse 装相应信息的类

        Cookie[] cookies =  request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("SESSION_ID")){
                    return cookie.getValue();
                }
            }
        }

        return  null;
    }

    /**
     *设置浏览器cookie
     * @param response
     * @param token
     */
    public static void writeLoginToken(HttpServletResponse response,String token){
        Cookie ck = new  Cookie("SESSION_ID",token);
        //设置cookie的域
//        ck.setDomain("jc.test.com");
//        //代表设在根目录
//        ck.setPath("/");
        //防止脚本读取
        ck.setHttpOnly(true);
        //单位是秒，设置成-1代表永久，如果cookie不设置maxage的话，cookie就不会写入硬盘，写在内存中，只在当前页面有效
        ck.setMaxAge(1000);
        response.addCookie(ck);
    }
}
