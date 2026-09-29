package br.com.aoyama.config;

import br.com.aoyama.security.CustomAuthenticationSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public SecurityConfig(CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Habilita o CORS para respeitar o WebMvcConfigurer que você criou
                .cors(Customizer.withDefaults())
                // Desativa o CSRF para permitir requisições POST/PUT de APIs
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(auth -> auth
                        // PERMITE O PREFLIGHT DO CORS (Método OPTIONS) SEM AUTENTICAÇÃO
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Rotas públicas de autenticação e login
                        .requestMatchers("/api/v1/auth/**", "/login/**", "/oauth2/**").permitAll()

                        // Rotas protegidas (Documentos, Pacientes, etc.)
                        .requestMatchers("/api/v1/documents/**", "/api/v1/patients/**").authenticated()

                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(customAuthenticationSuccessHandler) // <--- REGISTRA AQUI
                )
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout") // URL que o Angular vai chamar
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK); // Retorna 200 OK ao deslogar com sucesso
                        })
                        .invalidateHttpSession(true) // Destrói a sessão
                        .clearAuthentication(true)  // Limpa a autenticação
                        .deleteCookies("JSESSIONID") // Apaga o cookie de sessão do navegador
                );

        return http.build();
    }
}