package br.com.petweb.petweb.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Pedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idPedido;

    private LocalDate dataPedido;

    private Double totalPedido;

    // Relacionamento com cliente
    @ManyToOne
    @JoinColumn(name = "idCliente_fk")
    private Cliente cliente;

    // Relacionamento com Itens
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<ItemDoPedido> itens;

    // Método para calcular o Total
    public Double calcularTotal(){
        Double total = 0.0;
        if(itens != null){
            for(ItemDoPedido item : itens){
                total += item.getSubtotal();
            }
        }
        return total;
    }

    // Método para atualizar o total
    public void atualizarTotal(){
        this.totalPedido = calcularTotal();
    }

}
