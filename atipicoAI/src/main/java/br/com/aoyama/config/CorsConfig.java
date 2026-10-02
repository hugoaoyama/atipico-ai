package br.com.aoyama.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // Libera todas as rotas sob /api/
                        .allowedOrigins(
                                "http://localhost:4200",
                                "http://atipico-frontend-alb-799579755.sa-east-1.elb.amazonaws.com",
                                "https://atipicoai.com.br"
                        ) // Origem do Angular 20
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                        .allowedHeaders("*")
                        .allowCredentials(true); // Essencial para o cookie JSESSIONID trafegar
            }
        };
    }
}