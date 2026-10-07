package br.com.petweb.petweb.security;

import br.com.petweb.petweb.entity.Usuario;
import br.com.petweb.petweb.repository.UsuarioRepository;
import br.com.petweb.petweb.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationSecurityTests {
    @Test void publicRegistrationCannotOverwriteAccountOrBecomeAdmin() {
        var repository = mock(UsuarioRepository.class);
        var encoder = new BCryptPasswordEncoder();
        var service = new UsuarioService();
        ReflectionTestUtils.setField(service, "usuarioRepository", repository);
        ReflectionTestUtils.setField(service, "passwordEncoder", encoder);
        var submitted = new Usuario();
        submitted.setIdUsuario(1);
        submitted.setRole("ROLE_ADMIN");
        submitted.setSenhaUsuario("senha-do-usuario");
        service.save(submitted);
        assertNull(submitted.getIdUsuario());
        assertEquals("ROLE_USER", submitted.getRole());
        assertTrue(encoder.matches("senha-do-usuario", submitted.getSenhaUsuario()));
        verify(repository).save(submitted);
    }
}
