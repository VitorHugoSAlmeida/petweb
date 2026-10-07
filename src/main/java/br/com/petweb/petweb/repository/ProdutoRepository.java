package br.com.petweb.petweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.petweb.petweb.entity.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    
}
