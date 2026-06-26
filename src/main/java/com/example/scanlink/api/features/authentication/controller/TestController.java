package com.example.scanlink.api.features.authentication.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/public/hello")
    public String publicHello(){
        return "This is public API";
    }

    @GetMapping("/private/hello")
    public String privateHello(){
        String uid = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return "This is private API, your uid: " + uid;
    }

}
