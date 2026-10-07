package br.com.petweb.petweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.petweb.petweb.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    
}
