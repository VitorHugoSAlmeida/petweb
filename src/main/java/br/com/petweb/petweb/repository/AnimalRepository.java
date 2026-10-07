package br.com.petweb.petweb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.petweb.petweb.dto.ClienteAnimalDTO;
import br.com.petweb.petweb.entity.Animal;


public interface AnimalRepository extends JpaRepository<Animal, Integer> {
    
    @Query("""
            SELECT new br.com.petweb.petweb.dto.ClienteAnimalDTO(
                a.cliente.nomeCliente,
                a.cliente.telefoneCliente,
                a.nomeAnimal
            )
            FROM Animal a
            WHERE a.cliente IS NOT NULL
            """
    )
    List<ClienteAnimalDTO> buscarClienteAnimal();

}
