package br.com.petweb.petweb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.petweb.petweb.dto.ClienteAnimalDTO;
import br.com.petweb.petweb.entity.Animal;
import br.com.petweb.petweb.repository.AnimalRepository;

@Service
public class AnimalService {
   
    @Autowired
    private AnimalRepository repository;

    //método para salvar o animal
    public Animal save(Animal animal){
        return repository.save(animal);
    }

    //Método para listar todos os animals
    public List<Animal> findAll(){
        return repository.findAll();
    }
    
    //Método para buscar um animal pelo Id
    public Animal findById(Integer id){
        return repository.findById(id).orElse(null);
    }

    //Método para excluir um animal pelo id
    public void deleteById(Integer id){
        repository.deleteById(id);
    }

    //Consulta utilizando DTO
    public List<ClienteAnimalDTO> buscarClienteAnimal(){
        return repository.buscarClienteAnimal();
    }
}
