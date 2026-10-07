package br.com.petweb.petweb.entity;

import java.time.LocalDate;
import java.time.LocalTime;

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
public class Consulta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idConsulta;

    private LocalDate dataConsulta;

    private LocalTime horaConsulta;

    @Column(nullable = false, length = 150)
    private String diagnosticoConsulta;

    @Column(nullable = false, length = 150)
    private String tratamentoConsulta;

    @Column(nullable = false, length = 150)
    private Double valorConsulta;

    @Column(nullable = false, length = 150)
    private String observacoesConsulta;

    @ManyToOne
    @JoinColumn(name = "idAnimal_fk")
    private Animal animal;

    @ManyToOne
    @JoinColumn(name = "idVeterinario_fk")
    private Veterinario veterinario;

}
