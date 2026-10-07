package br.com.petweb.petweb.controller;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.petweb.petweb.entity.Usuario;
import br.com.petweb.petweb.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;

/**
 * Recuperação de senha por SMS em um único arquivo.
 * O código fica na SESSÃO do servidor (não vai para o navegador nem para o banco).
 */
@Controller
@RequestMapping("/auth/recuperar-senha")
public class RecuperarSenhaController {

    private static final String VIEW = "senha/recuperarSenha";
    private static final int VALIDADE_MIN = 10;
    private static final int MAX_TENTATIVAS = 5;
    private static final int ESPERA_REENVIO_SEG = 60;

    // chaves da sessão
    private static final String S_INICIADO = "rec.iniciado";
    private static final String S_USUARIO = "rec.usuarioId";
    private static final String S_CODIGO = "rec.codigo";
    private static final String S_EXPIRA = "rec.expira";
    private static final String S_TENTATIVAS = "rec.tentativas";
    private static final String S_ULTIMO_ENVIO = "rec.ultimoEnvio";
    private static final String S_VERIFICADO = "rec.verificado";

    // Deixe vazio para só mostrar o código no console (modo teste)
    @Value("${twilio.account-sid:}") private String twilioSid;
    @Value("${twilio.auth-token:}")  private String twilioToken;
    @Value("${twilio.from:}")        private String twilioFrom;

    private final SecureRandom random = new SecureRandom();
    private final HttpClient http = HttpClient.newHttpClient();

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public RecuperarSenhaController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ---------------- Etapa 1: usuário ou telefone ----------------

    @GetMapping
    public String etapa1(HttpSession session, Model model) {
        limpar(session);
        model.addAttribute("etapa", 1);
        return VIEW;
    }

    @PostMapping
    public String solicitar(@RequestParam String identificador, HttpSession session,
                            Model model, RedirectAttributes ra) {
        if (identificador.isBlank()) {
            model.addAttribute("etapa", 1);
            model.addAttribute("erro", "Informe seu usuário ou telefone.");
            return VIEW;
        }
        limpar(session);
        session.setAttribute(S_INICIADO, true);
        gerarEEnviar(identificador.trim(), session);
        ra.addFlashAttribute("info", "Se o cadastro existir, enviamos um código por SMS para o telefone cadastrado.");
        return "redirect:/auth/recuperar-senha/codigo";
    }

    // ---------------- Etapa 2: código ----------------

    @GetMapping("/codigo")
    public String etapa2(HttpSession session, Model model) {
        if (session.getAttribute(S_INICIADO) == null) return "redirect:/auth/recuperar-senha";
        model.addAttribute("etapa", 2);
        model.addAttribute("espera", ESPERA_REENVIO_SEG);
        return VIEW;
    }

    @PostMapping("/codigo")
    public String verificar(@RequestParam String codigo, HttpSession session, Model model) {
        if (session.getAttribute(S_INICIADO) == null) return "redirect:/auth/recuperar-senha";

        String esperado = (String) session.getAttribute(S_CODIGO);
        Instant expira = (Instant) session.getAttribute(S_EXPIRA);
        Integer tentativasSessao = (Integer) session.getAttribute(S_TENTATIVAS);
        int tentativas = tentativasSessao == null ? 0 : tentativasSessao;

        boolean valido = esperado != null && expira != null
                && Instant.now().isBefore(expira)
                && tentativas < MAX_TENTATIVAS
                && MessageDigest.isEqual(esperado.getBytes(StandardCharsets.UTF_8),
                                         codigo.trim().getBytes(StandardCharsets.UTF_8));
        session.setAttribute(S_TENTATIVAS, tentativas + 1);

        if (!valido) {
            model.addAttribute("etapa", 2);
            model.addAttribute("espera", ESPERA_REENVIO_SEG);
            model.addAttribute("erro", "Código inválido ou expirado. Confira o SMS ou peça um novo código.");
            return VIEW;
        }
        session.setAttribute(S_VERIFICADO, true);
        session.removeAttribute(S_CODIGO); // código só serve uma vez
        return "redirect:/auth/recuperar-senha/nova";
    }

    @PostMapping("/codigo/reenviar")
    public String reenviar(HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute(S_INICIADO) == null) return "redirect:/auth/recuperar-senha";

        Instant ultimo = (Instant) session.getAttribute(S_ULTIMO_ENVIO);
        if (ultimo == null || Instant.now().isAfter(ultimo.plusSeconds(ESPERA_REENVIO_SEG))) {
            Integer usuarioId = (Integer) session.getAttribute(S_USUARIO);
            if (usuarioId != null) {
                usuarioRepository.findById(usuarioId).ifPresent(u -> enviarNovoCodigo(u, session));
            }
        }
        ra.addFlashAttribute("info", "Pedido de novo código enviado. Aguarde o SMS.");
        return "redirect:/auth/recuperar-senha/codigo";
    }

    // ---------------- Etapa 3: nova senha ----------------

    @GetMapping("/nova")
    public String etapa3(HttpSession session, Model model) {
        if (session.getAttribute(S_VERIFICADO) == null) return "redirect:/auth/recuperar-senha";
        model.addAttribute("etapa", 3);
        return VIEW;
    }

    @PostMapping("/nova")
    public String redefinir(@RequestParam String novaSenha, @RequestParam String confirmacao,
                            HttpSession session, Model model) {
        Integer usuarioId = (Integer) session.getAttribute(S_USUARIO);
        if (session.getAttribute(S_VERIFICADO) == null || usuarioId == null) {
            return "redirect:/auth/recuperar-senha";
        }

        String erro = null;
        if (novaSenha.length() < 8) erro = "A senha precisa ter pelo menos 8 caracteres.";
        else if (!novaSenha.equals(confirmacao)) erro = "As senhas não são iguais.";
        if (erro != null) {
            model.addAttribute("etapa", 3);
            model.addAttribute("erro", erro);
            return VIEW;
        }

        Usuario u = usuarioRepository.findById(usuarioId).orElseThrow();
        definirSenha(u, passwordEncoder.encode(novaSenha));
        usuarioRepository.save(u);

        limpar(session);
        return "redirect:/login?senhaAlterada";
    }

    // ==================================================================
    // ADAPTADORES: se os nomes na sua entidade Usuario forem diferentes,
    // corrija apenas estas 3 linhas (e o método do repository).
    // ==================================================================
    private static Integer idDe(Usuario u)                { return u.getIdUsuario(); }
    private static String telefoneDe(Usuario u)           { return u.getTelefoneUsuario(); }
    private static void definirSenha(Usuario u, String h) { u.setSenhaUsuario(h); }

    // ---------------- Apoio ----------------

    private void gerarEEnviar(String identificador, HttpSession session) {
        Optional<Usuario> opt = usuarioRepository.findByLoginUsuario(identificador);
        if (opt.isEmpty()) {
            String digitos = identificador.replaceAll("\\D", "");
            if (digitos.length() >= 10) {
                if (digitos.startsWith("55") && digitos.length() > 11) digitos = digitos.substring(2);
                opt = usuarioRepository.findByTelefoneUsuario(digitos);
            }
        }
        opt.ifPresent(u -> enviarNovoCodigo(u, session));
    }

    private void enviarNovoCodigo(Usuario u, HttpSession session) {
        if (telefoneDe(u) == null || telefoneDe(u).isBlank()) return;

        String codigo = String.format("%06d", random.nextInt(1_000_000));
        session.setAttribute(S_USUARIO, idDe(u));
        session.setAttribute(S_CODIGO, codigo);
        session.setAttribute(S_EXPIRA, Instant.now().plus(Duration.ofMinutes(VALIDADE_MIN)));
        session.setAttribute(S_TENTATIVAS, 0);
        session.setAttribute(S_ULTIMO_ENVIO, Instant.now());

        String digitos = telefoneDe(u).replaceAll("\\D", "");
        if (digitos.startsWith("55") && digitos.length() > 11) digitos = digitos.substring(2);
        enviarSms("+55" + digitos,
                "PetWeb: seu código de recuperação é " + codigo + ". Vale por " + VALIDADE_MIN + " minutos.");
    }

    /** Sem credenciais Twilio configuradas, apenas mostra o SMS no console (teste). */
    private void enviarSms(String para, String texto) {
        if (twilioSid.isBlank()) {
            System.out.println("[SMS simulado] " + para + " -> " + texto);
            return;
        }
        try {
            String corpo = "To=" + enc(para) + "&From=" + enc(twilioFrom) + "&Body=" + enc(texto);
            String auth = Base64.getEncoder().encodeToString((twilioSid + ":" + twilioToken).getBytes(StandardCharsets.UTF_8));
            HttpRequest req = HttpRequest.newBuilder(
                            URI.create("https://api.twilio.com/2010-04-01/Accounts/" + twilioSid + "/Messages.json"))
                    .header("Authorization", "Basic " + auth)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(corpo))
                    .build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() >= 300) System.err.println("Falha ao enviar SMS: " + resp.body());
        } catch (Exception e) {
            System.err.println("Falha ao enviar SMS: " + e.getMessage());
        }
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private void limpar(HttpSession s) {
        for (String k : new String[]{S_INICIADO, S_USUARIO, S_CODIGO, S_EXPIRA, S_TENTATIVAS, S_ULTIMO_ENVIO, S_VERIFICADO}) {
            s.removeAttribute(k);
        }
    }
}