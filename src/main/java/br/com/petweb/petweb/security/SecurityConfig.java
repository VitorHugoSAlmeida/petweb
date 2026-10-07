package br.com.petweb.petweb.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final LoginSuccessHandler loginSuccessHandler;

    public SecurityConfig(LoginSuccessHandler loginSuccessHandler) {
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // 1) Públicas
                        .requestMatchers("/", "/login",
                                         "/petweb",
                                         "/css/**",
                                         "/js/**",
                                         "/images/**",
                                         "/acesso-negado",
                                         "/usuarios/criar",
                                         "/usuarios/salvar",
                                         "/auth/recuperar-senha/**")
                        .permitAll()

                        // 2) Somente ADMIN
                        .requestMatchers("/admin/**",
                                         "/usuarios/**",
                                         "/veterinarios/**",
                                         "/clientes/**",
                                         "/produtos/**",
                                         "/animais/consulta")
                        .hasRole("ADMIN")

                        // 3) Área exclusiva do usuário comum
                        .requestMatchers("/home", "/usuario/**",
                                         "/animais/**",
                                         "/consultas/**",
                                         "/pedidos/**")
                        .hasRole("USER")

                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(loginSuccessHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .exceptionHandling(ex -> ex.accessDeniedPage("/acesso-negado"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
