package com.catpp.oauth2.controller.oauth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * com.catpp.springbootjpa.controller.oauth
 *
 * @Author cat_pp
 * @Date 2018/12/28
 * @Description 这个控制器是需要获取授权Token后使用Token才可以访问到的
 */
@RestController
@RequestMapping("/secure")
public class SecureController {

    @RequestMapping("/say")
    public String sayHello() {
        return "Secure Hello User";
    }
}
