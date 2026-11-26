package cl.duoc.ferreteria.config;

import cl.duoc.ferreteria.enums.Rol;
import cl.duoc.ferreteria.models.User;
import cl.duoc.ferreteria.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev")
public class AdminInicialConfig {

    @Bean
    public CommandLineRunner crearAdministrador(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${ADMIN_PASSWORD}") String password) {

        return args -> {
            String email = "admin@losmaestros.cl";

            if (userRepository.existsByEmail(email)) {
                return;
            }

            User admin = new User();
            admin.setNombre("Administrador");
            admin.setApellido("Los Maestros");
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRol(Rol.ADMIN);

            userRepository.save(admin);
        };
    }
}