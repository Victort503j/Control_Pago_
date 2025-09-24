package com.controlpago.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class DatabaseWebSecurity {

    @Bean
    public UserDetailsManager customUsers(DataSource dataSource){
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        users.setUsersByUsernameQuery("select login, clave, status from usuarios where login = ?");
        users.setAuthoritiesByUsernameQuery("select u.login, r.nombre from usuario_rol ur " +
                "inner join usuarios u on u.id = ur.usuario_id " +
                "inner join roles r on r.id = ur.rol_id " +
                "where u.login = ?");
        return users;
    }

    // AÑADE ESTE BEAN
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                // Aperturar el acceso a los recursos estáticos
                .requestMatchers("/assets/**", "/css/**", "/js/**", "/images/**").permitAll()
                // Las vistas públicas no requieren autenticación
                .requestMatchers("/", "/privacy", "/terms", "/pagos/success*", "/pago/success*","/pagos/customerOk", "/pago/customerOk").permitAll()
                // Evitar que usuarios autenticados accedan a la página de login
                .requestMatchers("/login").not().fullyAuthenticated()
                // Asignar permisos a URLS por roles
                .requestMatchers("/alumnos/**").hasAnyAuthority("secretaria", "Secretaria")
                // Todas las demás vistas requieren autenticación
                .anyRequest().authenticated()
        );

        http.formLogin(form ->form
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
        );

        // Manejo de excepciones
        http.exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint((request, response, authException) ->
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden"))
        );

        return http.build();
    }
}
