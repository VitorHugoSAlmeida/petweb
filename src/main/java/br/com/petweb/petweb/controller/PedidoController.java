package br.com.petweb.petweb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.petweb.petweb.entity.Cliente;
import br.com.petweb.petweb.entity.Pedido;
import br.com.petweb.petweb.entity.Produto;
import br.com.petweb.petweb.service.ClienteService;
import br.com.petweb.petweb.service.PedidoService;
import br.com.petweb.petweb.service.ProdutoService;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {
    
    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ProdutoService produtoService;

    //Endpoint para salvar o pedido(JSON - pelo fetch)
    @PostMapping
    @ResponseBody
    public Pedido salvarPedido(@RequestBody Pedido pedido){
        return pedidoService.salvarPedido(pedido);
    }

    // Abrir a tela de cadastro de pedido
    @GetMapping("/criar")
    public String criarForm(Model model) {
        model.addAttribute("pedido", new Pedido());

        List<Cliente> clientes = clienteService.findAll();
        model.addAttribute("clientes", clientes);

        List<Produto> produtos = produtoService.findAll();
        model.addAttribute("produtos", produtos);

        return "pedido/formularioPedido";
    }

}
