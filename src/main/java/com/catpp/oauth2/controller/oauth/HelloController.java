package com.catpp.oauth2.controller.oauth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * com.catpp.springbootjpa.controller
 *
 * @Author cat_pp
 * @Date 2018/12/28
 * @Description
 */
@RestController
@RequestMapping("/hello")
public class HelloController {

    @RequestMapping("/say")
    public String sayHello() {
        return "hello User";
    }
}
