package br.com.petweb.petweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.petweb.petweb.entity.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Integer>{
    
}
