package br.com.petweb.petweb.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.petweb.petweb.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByLoginUsuario(String loginUsuario);

    // NOVO: o nome depois de "findBy" deve ser igual ao campo de telefone da entidade
    Optional<Usuario> findByTelefoneUsuario(String telefoneUsuario);
}