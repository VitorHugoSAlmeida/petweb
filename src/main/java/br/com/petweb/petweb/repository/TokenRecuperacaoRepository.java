package br.com.petweb.petweb.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.petweb.petweb.entity.TokenRecuperacao;
import br.com.petweb.petweb.entity.Usuario;

public interface TokenRecuperacaoRepository extends JpaRepository<TokenRecuperacao, Long> {

    Optional<TokenRecuperacao> findByTokenHash(String tokenHash);

    void deleteByUsuario(Usuario usuario);
}

/* -----------------------------------------------------------------
   No seu UsuarioRepository (já existente), adicione se não tiver:

   Optional<Usuario> findByEmailUsuario(String emailUsuario);
------------------------------------------------------------------ */