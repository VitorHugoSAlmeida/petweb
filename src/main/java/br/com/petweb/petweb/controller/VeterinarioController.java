package br.com.petweb.petweb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.petweb.petweb.entity.Veterinario;
import br.com.petweb.petweb.entity.Cliente;
import br.com.petweb.petweb.service.VeterinarioService;
import br.com.petweb.petweb.service.ClienteService;

@Controller
@RequestMapping("/veterinarios")
public class VeterinarioController {
    
    @Autowired
    private VeterinarioService service;

    @Autowired
    private ClienteService clienteService;

    //método para listar
    @GetMapping("/listar")
    public String listar(Model model){
        //Busca todos os veterinarios
        List<Veterinario> veterinarios = service.findAll();
        //Adiciona o veterinario ao modelo
        model.addAttribute("veterinarios",veterinarios);
        //retornar a página de formulário de veterinario
        return "veterinario/listarVeterinario";
    }

    //método pra abrir o formulário de cadastro de veterinario
    @GetMapping("/criar")
    public String criarForm(Model model){
        //adicionar um novo veterinario
        model.addAttribute("veterinario", new Veterinario());
        List<Cliente> clientes = clienteService.findAll();
        model.addAttribute("clientes", clientes);
        //retorna a página do formulário de veterinario
        return "veterinario/formularioVeterinario";
    }

    //método para salvar um veterinario
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Veterinario veterinario){
        //salvar veterinario
        service.save(veterinario);
        return "redirect:/veterinarios/listar";
    }

    //método para abrir o formulário de edição de veterinario
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model){
    //Buscar veterinario pelo id
    Veterinario veterinario = service.findByIdVeterinario(id);
    //adicionaro veterinario ao modelo
    model.addAttribute("veterinario", veterinario);
    List<Cliente> clientes = clienteService.findAll();
    model.addAttribute("clientes", clientes);
    //Retorna a página do formulário veterinario
    return "veterinario/formularioVeterinario";
   }

   //método para excluir veterinario
   @GetMapping("/excluir/{id}")
   public String excluir(@PathVariable Integer id){
    service.deleteById(id);//excluir o veterinario pelo id
     return "redirect:/veterinarios/listar";//redireciona para a lista de veterinarios

   }
}
