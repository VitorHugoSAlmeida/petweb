package br.com.petweb.petweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.petweb.petweb.entity.ItemDoPedido;

public interface ItemDoPedidoRepository extends JpaRepository<ItemDoPedido, Integer>{
    
}
