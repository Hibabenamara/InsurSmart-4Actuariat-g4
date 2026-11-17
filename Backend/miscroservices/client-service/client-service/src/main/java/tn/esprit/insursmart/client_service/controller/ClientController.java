package tn.esprit.insursmart.client_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @GetMapping("/hello")
    public String hello() {
        return "Client Service is running on port 8081!";
    }
}