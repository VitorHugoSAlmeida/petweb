package br.com.petweb.petweb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import br.com.petweb.petweb.dto.ClienteAnimalDTO;
import br.com.petweb.petweb.entity.Animal;
import br.com.petweb.petweb.entity.Cliente;
import br.com.petweb.petweb.service.AnimalService;
import br.com.petweb.petweb.service.ClienteService;

@Controller
@RequestMapping("/animais")
public class AnimalController {
    
    @Autowired
    private AnimalService animalService;

    @Autowired
    private ClienteService clienteService;

    //método para listar
    @GetMapping("/listar")
    public String listar(Model model){
        //Busca todos os animais
        List<Animal> animais = animalService.findAll();
        //Adiciona o animal ao modelo
        model.addAttribute("animais",animais);
        //retornar a página de formulário de animal
        return "animal/listarAnimal";
    }

    //método pra abrir o formulário de cadastro de animal
    @GetMapping("/criar")
    public String criarForm(Model model){
        //adicionar um novo animal
        model.addAttribute("animal", new Animal());
        List<Cliente> clientes = clienteService.findAll();
        model.addAttribute("clientes", clientes);
        //retorna a página do formulário de animal
        return "animal/formularioAnimal";
    }

    //método para salvar um animal
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Animal animal,
        @RequestParam("foto") MultipartFile foto) {
        
        try{
            if(!foto.isEmpty()){
                animal.setFotoAnimal(foto.getBytes());
                animal.setTipoFoto(foto.getContentType());
            }
            animalService.save(animal);
        } catch (Exception ex){
            ex.printStackTrace();
        }

        //salvar animal
        animalService.save(animal);
        return "redirect:/animais/listar";
    }

    //método para abrir o formulário de edição de animal
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model){
    //Buscar animal pelo id
    Animal animal = animalService.findById(id);
    //adicionaro animal ao modelo
    model.addAttribute("animal", animal);
    List<Cliente> clientes = clienteService.findAll();
    model.addAttribute("clientes", clientes);
    //Retorna a página do formulário animal
    return "animal/formularioAnimal";
   }

   //método para excluir animal
   @GetMapping("/excluir/{id}")
   public String excluir(@PathVariable Integer id){
    animalService.deleteById(id);//excluir o animal pelo id
     return "redirect:/animais/listar";//redireciona para a lista de animais

   }

   @GetMapping("/foto/{id}")
   public ResponseEntity<byte[]> foto(@PathVariable Integer id) {

        Animal animal = animalService.findById(id);
        if(animal == null || animal.getFotoAnimal() == null){
            return ResponseEntity.notFound().build();
        }else if (animal.getIdAnimal() != null){
            Animal animalExistente = animalService.findById(animal.getIdAnimal());
            if(animalExistente != null){
                animal.setFotoAnimal(animalExistente.getFotoAnimal());
                animal.setTipoFoto(animalExistente.getTipoFoto());
            }
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(animal.getTipoFoto()))
            .body(animal.getFotoAnimal());
   }

   //Método para listar Clientes e Animais via DTO
   @GetMapping("/consulta")
    public String consulta(Model model){
        List<ClienteAnimalDTO> resultados = animalService.buscarClienteAnimal();
        model.addAttribute("resultados", resultados);
        return "animal/consultaClientesAnimais";
    }
   
}