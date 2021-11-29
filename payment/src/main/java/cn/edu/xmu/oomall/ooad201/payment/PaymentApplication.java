package cn.edu.xmu.oomall.ooad201.payment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author xiuchen lang
 */
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.oomall.core", "cn.edu.xmu.oomall.ooad201.payment","cn.edu.xmu.privilegegateway"})
@MapperScan("cn.edu.xmu.oomall.ooad201.payment.mapper")
//@EnableFeignClients(basePackages = "cn.edu.xmu.oomall.activity.microservice")
public class PaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }
}
