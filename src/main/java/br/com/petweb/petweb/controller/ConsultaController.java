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

import br.com.petweb.petweb.entity.Animal;
import br.com.petweb.petweb.entity.Consulta;
import br.com.petweb.petweb.entity.Veterinario;
import br.com.petweb.petweb.service.AnimalService;
import br.com.petweb.petweb.service.ConsultaService;
import br.com.petweb.petweb.service.VeterinarioService;


@Controller
@RequestMapping("/consultas")
public class ConsultaController {
    
    //injeção de depêndencia da service de consultas
    @Autowired
    private ConsultaService consultaService;

    //injeção de depêndencia da service de animais
    @Autowired
    private AnimalService animalService;

    //injeção de depêndencia da service de veterinarios
    @Autowired
    private VeterinarioService veterinarioService;

    //Método para salvar um consulta
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Consulta consulta) {
        consultaService.save(consulta);
        return "redirect:/consultas/listar";
    }

    //Método para listar todos os consultas
    @GetMapping("/listar")
    public String listar(Model model) {
        List<Consulta> consultas = consultaService.findAll();
        model.addAttribute("consultas", consultas);
        return "consulta/listarConsultas";
    }

    //Método para exibir o formulário de cadastro de consulta
    @GetMapping("/criar")
    public String criar(Model model) {
        model.addAttribute("consulta", new Consulta());
        List<Animal> animais = animalService.findAll();
        model.addAttribute("animais", animais);
        List<Veterinario> veterinarios = veterinarioService.findAll();
        model.addAttribute("veterinarios", veterinarios);
        return "consulta/formularioConsulta";
    }
    //método para excluir consulta por ID
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id ) {
       consultaService.deletById(id);
        return "redirect:/consultas/listar";
    }
    //Método para editar o formulario consulta
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Consulta consulta = consultaService.findById(id);
        model.addAttribute("consulta", consulta);
        List<Animal> animais = animalService.findAll();
        model.addAttribute("animais", animais);
        List<Veterinario> veterinarios = veterinarioService.findAll();
        model.addAttribute("veterinarios", veterinarios);
        return "consulta/formularioConsulta";
    }
    
}
