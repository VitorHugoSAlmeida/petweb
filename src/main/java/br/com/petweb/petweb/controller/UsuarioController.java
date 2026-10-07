package br.com.petweb.petweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.petweb.petweb.entity.Usuario;
import br.com.petweb.petweb.service.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("salvar")
    public String salvar(@ModelAttribute Usuario usuario) {
        // O cadastro público nunca concede privilégios administrativos.
        usuario.setIdUsuario(null);
        usuario.setRole("ROLE_USER");
        usuarioService.save(usuario);
        return "/login";
    }

    @GetMapping("/criar")
    public String criarForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "/usuario/formularioUsuario";
    }

}
