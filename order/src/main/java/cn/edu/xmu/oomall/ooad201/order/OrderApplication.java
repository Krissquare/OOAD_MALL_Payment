package cn.edu.xmu.oomall.ooad201.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author xiuchen lang
 */
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.oomall.core", "cn.edu.xmu.oomall.ooad201.order","cn.edu.xmu.privilegegateway"})
@MapperScan("cn.edu.xmu.oomall.ooad201.order.mapper")
@EnableFeignClients(basePackages = "cn.edu.xmu.oomall.ooad201.order.microService")
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
