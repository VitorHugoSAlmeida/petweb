package br.com.petweb.petweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.petweb.petweb.service.RecuperacaoSenhaService;

@Controller
public class Recuperacaosenhacontroller {

    private static final String TELA_ESQUECI = "recuperacao/esqueciSenha";
    private static final String TELA_REDEFINIR = "recuperacao/redefinirSenha";

    @Autowired
    private RecuperacaoSenhaService service;

    // Tela onde o usuário digita o e-mail
    @GetMapping("/esqueci-senha")
    public String formEsqueci() {
        return TELA_ESQUECI;
    }

    // Recebe o e-mail. A resposta é sempre a mesma, exista ou não o e-mail.
    @PostMapping("/esqueci-senha")
    public String solicitar(@RequestParam("email") String email, Model model) {
        service.solicitar(email);
        model.addAttribute("enviado", true);
        return TELA_ESQUECI;
    }

    // Link do e-mail: /redefinir-senha?token=...
    @GetMapping("/redefinir-senha")
    public String formRedefinir(@RequestParam(value = "token", required = false) String token, Model model) {
        prepararTelaRedefinir(token, model);
        return TELA_REDEFINIR;
    }

    @PostMapping("/redefinir-senha")
    public String redefinir(@RequestParam("token") String token,
                            @RequestParam("senha") String senha,
                            @RequestParam("confirmacao") String confirmacao,
                            Model model,
                            RedirectAttributes redirect) {
        try {
            service.redefinir(token, senha, confirmacao);
            redirect.addFlashAttribute("mensagem", "Senha alterada com sucesso. Faça login com a nova senha.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            prepararTelaRedefinir(token, model);
            return TELA_REDEFINIR;
        }
    }

    private void prepararTelaRedefinir(String token, Model model) {
        if (service.tokenValido(token)) {
            model.addAttribute("token", token);
        } else {
            model.addAttribute("tokenInvalido", true);
        }
    }
}