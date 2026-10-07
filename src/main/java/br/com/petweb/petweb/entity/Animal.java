package br.com.petweb.petweb.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Animal {
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idAnimal;

    @Column(nullable = false, length = 25)
    private String nomeAnimal;

    @Column(nullable = false, length = 15)
    private String racaAnimal;
    
    @Column(nullable = false, length = 15)
    private String porteAnimal;
    
    @Column(nullable = false)
    private Double pesoAnimal;

    @Lob
    private byte[] fotoAnimal;

    @Column(length = 30)
    private String tipoFoto;

    @ManyToOne
    @JoinColumn(name = "idCliente_fk")
    private Cliente cliente;
}
