package cn.edu.xmu.oomall.ordermq;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/24 15:55
 */
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.oomall.core.util","cn.edu.xmu.oomall.core.model","cn.edu.xmu.oomall.ordermq", "cn.edu.xmu.privilegegateway"})
@MapperScan("cn.edu.xmu.oomall.ordermq.mapper")
@EnableFeignClients(basePackages = "cn.edu.xmu.oomall.ordermq.microservice")
public class OrderMqApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderMqApplication.class, args);
    }
}
