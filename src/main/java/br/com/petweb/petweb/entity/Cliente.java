package br.com.petweb.petweb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idCliente;

    @Column(nullable = false, length = 40)
    private String nomeCliente;

    @Column(nullable = false, length = 100)
    private String emailCliente;

    @Column(nullable = false, length = 30)
    private String telefoneCliente;

    @Column(nullable = false, length = 50)
    private String enderecoCliente;

    @Column(nullable = false, length = 14)
    private String cpfCliente;


}
