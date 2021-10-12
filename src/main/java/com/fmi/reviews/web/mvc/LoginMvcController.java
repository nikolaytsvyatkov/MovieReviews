package com.fmi.reviews.web.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginMvcController {

    @GetMapping("/login")
    String getLogin(){
        return "login";
    }
}
