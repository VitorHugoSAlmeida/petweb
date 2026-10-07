package br.com.petweb.access;

import br.com.petweb.petweb.security.SecurityConfig;
import br.com.petweb.petweb.security.UserDetailsImpl;

import br.com.petweb.petweb.controller.LoginController;
import br.com.petweb.petweb.controller.UsuarioController;
import br.com.petweb.petweb.entity.Usuario;
import br.com.petweb.petweb.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = AdminAccessTests.TestConfig.class)
class AdminAccessTests {
    @Autowired WebApplicationContext context;
    @Autowired UsuarioService usuarioService;
    MockMvc mvc;

    @BeforeEach void setup() {
        reset(usuarioService);
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test void adminUsesSameViewAsUser() throws Exception {
        mvc.perform(get("/admin").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk()).andExpect(view().name("home"))
                .andExpect(model().attribute("administrador", true));
        mvc.perform(get("/home").with(user("user").roles("USER")))
                .andExpect(view().name("home"))
                .andExpect(model().attribute("administrador", false));
    }

    @Test void adminIsDirectedToAdminPage() throws Exception {
        mvc.perform(get("/home").with(user("admin").roles("ADMIN")))
                .andExpect(redirectedUrl("/admin"));
    }

    @Test void userCannotOpenAdminPage() throws Exception {
        mvc.perform(get("/admin").with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/admin")).andExpect(redirectedUrl("/login"));
    }

    @Test void adminCanAccessAllModulesAndWrite() throws Exception {
        for (String module : new String[]{"clientes", "animais", "veterinarios", "consultas", "produtos", "pedidos"}) {
            mvc.perform(get("/" + module + "/listar").with(user("admin").roles("ADMIN")))
                    .andExpect(status().isOk());
            mvc.perform(post("/" + module + "/salvar").with(user("admin").roles("ADMIN")))
                    .andExpect(status().isOk());
        }
    }

    @Test void anonymousCannotOpenModules() throws Exception {
        mvc.perform(get("/clientes/listar")).andExpect(redirectedUrl("/login"));
    }

    @Test void customLoginAndAuthenticationWork() throws Exception {
        mvc.perform(get("/login")).andExpect(view().name("login"));
        mvc.perform(formLogin().user("admin").password("password"))
                .andExpect(redirectedUrl("/home"));
    }

    @Test void publicRegistrationCannotGrantAdminOrOverwriteUser() throws Exception {
        mvc.perform(post("/usuarios/salvar").param("role", "ROLE_ADMIN")
                .param("idUsuario", "1").param("loginUsuario", "novo"))
                .andExpect(status().isOk());
        verify(usuarioService).save(argThat(u -> "ROLE_USER".equals(u.getRole()) && u.getIdUsuario() == null));
    }

    @Test void adminAuthorityIncludesUserAndAcceptsLegacyRole() {
        Usuario usuario = new Usuario();
        usuario.setRole("admin");
        var authorities = new UserDetailsImpl(usuario).getAuthorities().stream()
                .map(a -> a.getAuthority()).toList();
        assertEquals(java.util.List.of("ROLE_ADMIN", "ROLE_USER"), authorities);
    }

    @TestConfiguration
    @EnableWebMvc
    @EnableWebSecurity
    @Import({SecurityConfig.class, LoginController.class, UsuarioController.class, ModuleEndpoints.class})
    static class TestConfig {
        @Bean UserDetailsService users() {
            return new InMemoryUserDetailsManager(User.withUsername("admin")
                    .password(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("password"))
                    .roles("ADMIN").build());
        }
        @Bean br.com.petweb.petweb.repository.UsuarioRepository usuarioRepository() {
            return mock(br.com.petweb.petweb.repository.UsuarioRepository.class);
        }
        @Bean UsuarioService usuarioService() { return mock(UsuarioService.class); }
        @Bean InternalResourceViewResolver viewResolver() {
            return new InternalResourceViewResolver("/WEB-INF/", ".html");
        }
    }

    // Isola a autorização HTTP dos serviços que dependem do PostgreSQL.
    @RestController
    static class ModuleEndpoints {
        @RequestMapping({"/clientes/**", "/animais/**", "/veterinarios/**", "/consultas/**", "/produtos/**", "/pedidos/**"})
        String module() { return "ok"; }
    }
}
