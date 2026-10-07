package br.com.petweb.petweb.security;

import br.com.petweb.petweb.controller.LoginController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = AccessControlTests.TestConfig.class)
class AccessControlTests {
    @Configuration
    @EnableWebMvc
    @EnableWebSecurity
    @Import({SecurityConfig.class, LoginSuccessHandler.class, LoginController.class, Modules.class})
    static class TestConfig {
        @Bean UserDetailsService users(org.springframework.security.crypto.password.PasswordEncoder encoder) {
            return new InMemoryUserDetailsManager(
                User.withUsername("admin").password(encoder.encode("senha")).roles("ADMIN").build(),
                User.withUsername("usuario").password(encoder.encode("senha")).roles("USER").build());
        }
        @Bean InternalResourceViewResolver views() {
            return new InternalResourceViewResolver("/templates/", ".html");
        }
    }
    @RestController
    static class Modules {
        @RequestMapping({"/clientes/listar", "/veterinarios/listar", "/produtos/listar",
            "/animais/listar", "/animais/consulta", "/consultas/listar", "/pedidos/criar", "/pedidos"})
        String module() { return "ok"; }
    }
    @Autowired WebApplicationContext context;
    @Autowired FilterChainProxy filters;
    MockMvc mvc;
    @BeforeEach void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).addFilters(filters).build();
    }
    MockHttpSession login(String name, String destination) throws Exception {
        return (MockHttpSession) mvc.perform(post("/login").param("username", name).param("password", "senha"))
            .andExpect(status().isFound()).andExpect(redirectedUrl(destination))
            .andReturn().getRequest().getSession(false);
    }
    @Test void adminHasOnlyAdminArea() throws Exception {
        var session = login("admin", "/admin/home");
        mvc.perform(get("/admin/home").session(session)).andExpect(status().isOk()).andExpect(view().name("admim"));
        for (String route : new String[]{"/clientes/listar", "/veterinarios/listar", "/produtos/listar", "/animais/consulta"})
            mvc.perform(get(route).session(session)).andExpect(status().isOk());
        for (String route : new String[]{"/home", "/usuario/home", "/animais/listar", "/consultas/listar", "/pedidos/criar"})
            mvc.perform(get(route).session(session)).andExpect(status().isForbidden());
        mvc.perform(post("/pedidos").session(session)).andExpect(status().isForbidden());
    }
    @Test void userHasOnlyUserArea() throws Exception {
        var session = login("usuario", "/home");
        mvc.perform(get("/home").session(session)).andExpect(status().isOk()).andExpect(view().name("home"));
        mvc.perform(get("/usuario/home").session(session)).andExpect(status().isOk());
        for (String route : new String[]{"/animais/listar", "/consultas/listar", "/pedidos/criar"})
            mvc.perform(get(route).session(session)).andExpect(status().isOk());
        for (String route : new String[]{"/admin/home", "/clientes/listar", "/veterinarios/listar", "/produtos/listar", "/animais/consulta"})
            mvc.perform(get(route).session(session)).andExpect(status().isForbidden());
        mvc.perform(post("/produtos/salvar").session(session)).andExpect(status().isForbidden());
    }
    @Test void anonymousMustLoginAndLogoutEndsSession() throws Exception {
        mvc.perform(get("/admin/home")).andExpect(status().isFound()).andExpect(redirectedUrl("/login"));
        mvc.perform(get("/home")).andExpect(status().isFound());
        mvc.perform(get("/login")).andExpect(status().isOk());
        var session = login("usuario", "/home");
        mvc.perform(post("/logout").session(session)).andExpect(redirectedUrl("/login?logout"));
        org.junit.jupiter.api.Assertions.assertTrue(session.isInvalid());
    }
}


