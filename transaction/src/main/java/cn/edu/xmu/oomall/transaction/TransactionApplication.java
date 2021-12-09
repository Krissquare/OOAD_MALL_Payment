package cn.edu.xmu.oomall.transaction;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author xiuchen lang
 */
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.oomall.core", "cn.edu.xmu.oomall.transaction","cn.edu.xmu.privilegegateway"})
@MapperScan("cn.edu.xmu.oomall.transaction.mapper")
@EnableFeignClients(basePackages = "cn.edu.xmu.oomall.transaction.microservice")
public class TransactionApplication {
    public static void main(String[] args) {
        SpringApplication.run(TransactionApplication.class, args);
    }
}
