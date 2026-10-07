package br.com.petweb.petweb.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.petweb.petweb.entity.TokenRecuperacao;
import br.com.petweb.petweb.entity.Usuario;
import br.com.petweb.petweb.repository.TokenRecuperacaoRepository;
import br.com.petweb.petweb.repository.UsuarioRepository;

@Service
public class RecuperacaoSenhaService {

    private static final Logger log = LoggerFactory.getLogger(RecuperacaoSenhaService.class);
    private static final int VALIDADE_MINUTOS = 30;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenRecuperacaoRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    /* ---------------------------------------------------------
       1. SOLICITAR: gera token e envia o e-mail.
       Não devolve nada de propósito: quem chama não deve saber
       se o e-mail existe ou não.
    --------------------------------------------------------- */
    @Transactional
    public void solicitar(String email) {
        if (email == null || email.isBlank()) {
            return;
        }

        Optional<Usuario> encontrado = usuarioRepository.findByEmailUsuario(email.trim());
        if (encontrado.isEmpty()) {
            return;
        }

        Usuario usuario = encontrado.get();

        // Só um link válido por vez
        tokenRepository.deleteByUsuario(usuario);

        String token = gerarToken();

        TokenRecuperacao registro = new TokenRecuperacao();
        registro.setTokenHash(hash(token));
        registro.setUsuario(usuario);
        registro.setExpiraEm(LocalDateTime.now().plusMinutes(VALIDADE_MINUTOS));
        tokenRepository.save(registro);

        String link = baseUrl + "/redefinir-senha?token=" + token;
        String texto = "Olá, " + usuario.getNomeUsuario() + "!\n\n"
                + "Recebemos um pedido para redefinir a sua senha no PetWeb.\n"
                + "Acesse o link abaixo (válido por " + VALIDADE_MINUTOS + " minutos):\n\n"
                + link + "\n\n"
                + "Se você não fez esse pedido, ignore este e-mail: a sua senha continua a mesma.";

        try {
            emailService.enviar(usuario.getEmailUsuario(), "PetWeb - Redefinição de senha", texto);
        } catch (Exception e) {
            // Não propaga: a tela responde igual, exista o e-mail ou não
            log.error("Falha ao enviar e-mail de recuperação", e);
        }
    }

    /* ---------------------------------------------------------
       2. VALIDAR TOKEN
    --------------------------------------------------------- */
    public boolean tokenValido(String token) {
        return buscarValido(token).isPresent();
    }

    private Optional<TokenRecuperacao> buscarValido(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        return tokenRepository.findByTokenHash(hash(token))
                .filter(t -> t.getExpiraEm().isAfter(LocalDateTime.now()));
    }

    /* ---------------------------------------------------------
       3. REDEFINIR SENHA
    --------------------------------------------------------- */
    @Transactional
    public void redefinir(String token, String novaSenha, String confirmacao) {
        TokenRecuperacao registro = buscarValido(token)
                .orElseThrow(() -> new IllegalArgumentException("Link inválido ou expirado. Solicite um novo."));

        if (novaSenha == null || !novaSenha.equals(confirmacao)) {
            throw new IllegalArgumentException("As senhas não coincidem.");
        }

        validarForcaDaSenha(novaSenha);

        Usuario usuario = registro.getUsuario();
        usuario.setSenhaUsuario(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        // Token de uso único
        tokenRepository.delete(registro);
    }

    // Mesmas regras do cadastro (validação no servidor, não só no JavaScript)
    private void validarForcaDaSenha(String senha) {
        boolean ok = senha.length() >= 8
                && senha.matches(".*[A-Z].*")
                && senha.matches(".*[a-z].*")
                && senha.matches(".*[0-9].*")
                && senha.matches(".*[^A-Za-z0-9].*");

        if (!ok) {
            throw new IllegalArgumentException(
                "A senha precisa ter 8 caracteres ou mais, com maiúscula, minúscula, número e caractere especial.");
        }
    }

    /* ---------------------------------------------------------
       Utilitários
    --------------------------------------------------------- */
    private String gerarToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}