package br.com.petweb.petweb.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String home(Authentication authentication, Model model) {
        if (authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()))) {
            return "redirect:/admin";
        }
        model.addAttribute("administrador", false);
        return "home";
    }

    @GetMapping({"/admin", "/admin/"})
    public String admin(Model model) {
        model.addAttribute("administrador", true);
        return "home";
    }
}
