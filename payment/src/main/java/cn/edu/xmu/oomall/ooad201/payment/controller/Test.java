package cn.edu.xmu.oomall.ooad201.payment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/11/29 20:42
 */
@RestController
public class Test {


    @GetMapping("/hello")
    public String test(){
        return "hello";
    }
}
