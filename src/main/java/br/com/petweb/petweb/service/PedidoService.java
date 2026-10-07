package br.com.petweb.petweb.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.petweb.petweb.entity.ItemDoPedido;
import br.com.petweb.petweb.entity.Pedido;
import br.com.petweb.petweb.entity.Produto;
import br.com.petweb.petweb.repository.PedidoRepository;
import br.com.petweb.petweb.repository.ProdutoRepository;

@Service
public class PedidoService {
    
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    //Método para salvar um pedido
    public Pedido salvarPedido(Pedido pedido){

        pedido.setDataPedido(LocalDate.now());

        for(ItemDoPedido item : pedido.getItens()){

            Produto produto = produtoRepository.findById(item.getProduto().getIdProduto()).orElseThrow(()
            -> new RuntimeException("Produto não encontrado"));

            item.setProduto(produto);

            item.setPreco(produto.getValorProduto());

            item.atualizarSubtotal();

            item.setPedido(pedido);
        }
        pedido.atualizarTotal();
        return pedidoRepository.save(pedido);

    }

}
