package br.com.petweb.petweb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.petweb.petweb.entity.Usuario;
import br.com.petweb.petweb.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario save(Usuario usuario) {
        // Cadastro público sempre cria uma conta; nunca altera uma conta existente.
        usuario.setIdUsuario(null);
        usuario.setSenhaUsuario(passwordEncoder.encode(usuario.getSenhaUsuario()));
        usuario.setRole("ROLE_USER"); // ignora qualquer role enviada pelo formulário
        return usuarioRepository.save(usuario);
    }
}
