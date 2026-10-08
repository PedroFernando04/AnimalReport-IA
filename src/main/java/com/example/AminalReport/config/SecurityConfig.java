package com.example.AminalReport.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // Adicionado para boas práticas
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity // Boa prática ao configurar o Spring Security
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        return http
                // 1. Configurações de segurança básicas
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable) // Desabilita CSRF (geralmente ok para APIs, mas cuidado em apps tradicionais)

                // 2. Autorização de Requisições
                .authorizeHttpRequests(authorize -> authorize
                        // Permite acesso irrestrito a recursos estáticos e URLs de autenticação/registro
                        .requestMatchers("/home/**","/entrar/**", "/registrar/**","/registrarOrg/**", "/css/**", "/js/**", "/images/**", "/denuncia", "/","/adocao/**", "/uploads/**").permitAll()
                        .anyRequest().authenticated() // Qualquer outra requisição exige autenticação
                )

                // 3. Configuração de Formulário de Login
                .formLogin(form -> form
                        .loginPage("/entrar")
                        .loginProcessingUrl("/entrar")
                        .usernameParameter("email")
                        .passwordParameter("senha")
                        .failureUrl("/entrar?error=true")
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                )

                // 4. Configuração de Logout
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL para disparar o logout (GET ou POST)
                        .logoutSuccessUrl("/entrar?logoutSuccess=true") // Redireciona após o logout
                        .deleteCookies("JSESSIONID")
                )

                // Constrói e retorna o SecurityFilterChain
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}