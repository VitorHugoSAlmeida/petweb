package br.com.petweb.petweb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.petweb.petweb.entity.Consulta;
import br.com.petweb.petweb.repository.ConsultaRepository;

@Service
public class ConsultaService {
    
    //injeção de depêndencia do repositório de consultas
    @Autowired
    private ConsultaRepository consultaRepository;

    //Método para salvar um consulta
    public Consulta save(Consulta consulta) {
        return consultaRepository.save(consulta);
    }

    //Método para listar todos os consultas
    public List<Consulta> findAll() {
        return consultaRepository.findAll();
    }
    //Método para excluir um consulta po ID
    public void deletById(Integer id){
        consultaRepository.deleteById(id);
    }
    //Método para encontrar um consulta por ID
    public Consulta findById(Integer id){
        return consultaRepository.findById(id).orElse(null);
    }
}
