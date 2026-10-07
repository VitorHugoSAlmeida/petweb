package br.com.petweb.petweb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class PetwebApplicationTests {
    @Autowired WebApplicationContext context;
    MockMvc mvc;

    @BeforeEach void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test void adminAndUserHaveIdenticalCss() throws Exception {
        String admin = mvc.perform(get("/admin").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String regular = mvc.perform(get("/home").with(user("user").roles("USER")))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertTrue(admin.contains("Painel do administrador"));
        assertTrue(regular.contains("Painel de módulos"));
        assertEquals(css(regular), css(admin));
        for (String module : new String[]{"clientes", "animais", "veterinarios", "consultas", "produtos", "pedidos"}) {
            assertTrue(admin.contains("/" + module + "/"));
        }
    }

    @Test void adminCanRenderExistingModules() throws Exception {
        for (String path : new String[]{"/clientes/listar", "/animais/listar", "/veterinarios/listar",
                "/consultas/listar", "/produtos/listar", "/pedidos/criar", "/animais/consulta"}) {
            mvc.perform(get(path).with(user("admin").roles("ADMIN"))).andExpect(status().isOk());
        }
    }

    private String css(String html) {
        assertTrue(html.contains("<style>"));
        return html.substring(html.indexOf("<style>"), html.indexOf("</style>"));
    }
}
