package br.com.petweb.petweb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.petweb.petweb.entity.Produto;
import br.com.petweb.petweb.service.ProdutoService;

    @Controller
    @RequestMapping("/produtos")
    public class ProdutoController {
        
    @Autowired
    private ProdutoService produtoService;

    //Método para listar todos os produtos
    @GetMapping("/listar")
    public String listar(Model model) {
        List<Produto> produtos = produtoService.findAll();
        model.addAttribute("produtos", produtos);
        return "produto/listarProdutos";
    }

    //Método para abrir o formulário de criação de produtos
    @GetMapping("/criar")
    public String criarForm(Model model) {
        model.addAttribute("produto", new Produto());
        return "produto/formularioProduto";
    }

    //Método para salvar um produto
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Produto produto) {
        produtoService.save(produto);
        return "redirect:/produtos/listar";
    }

    //Método para abrir o formulário de adição de produtos
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        Produto produto = produtoService.findById(id);
        model.addAttribute("produto", produto);
        return "produto/formularioProduto";
    }

    //Método para atualizar um produto
    @PutMapping("/atualizar/{id}")
    public String atualizar(@PathVariable Integer id, @ModelAttribute Produto produto) {
        produto.setIdProduto(id);
        produtoService.save(produto);
        return "redirect:/produtos/listar";
    }

    //Método para excluir um produto pelo ID
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        produtoService.deleteById(id);
        return "redirect:/produtos/listar";
    }
}
