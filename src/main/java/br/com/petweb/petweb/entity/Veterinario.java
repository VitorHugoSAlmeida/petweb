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
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Veterinario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idVeterinario;

    @Column(nullable = false, length = 40)
    private String nomeVeterinario;

    @Column(nullable = false, length = 14)
    private String cpfVeterinario;
    
    @Column(nullable = false, length = 30)
    private String telefoneVeterinario;

    @Column(nullable = false)
    private Integer crmVeterinario;

    @Column(nullable = false, length = 40)
    private String especialidadeVeterinario;
}
