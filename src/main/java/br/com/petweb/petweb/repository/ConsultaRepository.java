package br.com.petweb.petweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.petweb.petweb.entity.Consulta;

public interface ConsultaRepository extends JpaRepository<Consulta, Integer> {
    
}
