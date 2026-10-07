package br.com.petweb.petweb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.petweb.petweb.entity.Veterinario;
import br.com.petweb.petweb.repository.VeterinarioRepository;

@Service
public class VeterinarioService {
   
    @Autowired
    private VeterinarioRepository repository;

    //método para salvar o animal
    public Veterinario save(Veterinario animal){
        return repository.save(animal);
    }

    //Método para listar todos os animals
    public List<Veterinario> findAll(){
        return repository.findAll();
    }
    
    //Método para buscar um animal pelo Id
    public Veterinario findByIdVeterinario(Integer id){
        return repository.findById(id).orElse(null);
    }

    //Método para excluir um animal pelo id
    public void deleteById(Integer id){
        repository.deleteById(id);
    }
}
