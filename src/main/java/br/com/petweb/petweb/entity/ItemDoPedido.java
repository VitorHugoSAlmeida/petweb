package br.com.petweb.petweb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDoPedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idItem;

    // Relacionamento com Pedido
    @ManyToOne
    @JoinColumn(name = "idPedido_fk")
    private Pedido pedido;

    //Relacionamento com Produto
    @ManyToOne
    @JoinColumn(name = "idProduto_fk")
    private Produto produto;

    @Column(nullable = false, length = 30)
    private Integer quantidade;

    @Column(nullable = false, length = 50)
    private Double preco;

    @Column(nullable = false, length = 50)
    private Double subtotal;

    // Método calcular subtotal
    public Double calcularSubtotal(){
        return quantidade * preco;
    }

    // Método para atualizar subtotal
    public void atualizarSubtotal(){
        this.subtotal = calcularSubtotal();
    }
}
