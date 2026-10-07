package br.com.petweb.petweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({"/", "/petweb"})
public class PetWebController {
    
    @GetMapping
    public String index(Model model) {
        return "index";
    }
}
