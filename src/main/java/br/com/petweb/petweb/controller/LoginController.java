package br.com.petweb.petweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping({"/home", "/usuario/home"})
    public String home() {
        return "home";
    }

    @GetMapping("/admin/home")
    public String adminHome() {
         return "admim";
    }

    @GetMapping("/acesso-negado")
    public String acessoNegado() {
        return "acesso-negado";
    }
}
