package com.example.springfordocker;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class WelcomeController {
    @GetMapping("/message")
    public String getMessage()
    {
        return "welcome Praveen!!!";
    }
}
