package cl.duoc.ferreteria;

import cl.duoc.ferreteria.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
    "spring.profiles.active=test",
    "spring.datasource.url=jdbc:h2:mem:migracion_test;DB_CLOSE_DELAY=-1",
    "spring.datasource.username=sa", "spring.datasource.password=",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.sql.init.mode=always", "spring.sql.init.schema-locations=classpath:usuarios-anteriores.sql",
    "spring.jpa.hibernate.ddl-auto=update", "spring.jpa.defer-datasource-initialization=false",
    "application.security.jwt.secret-key=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY="
})
class MigracionUsuariosTests {
    @Autowired UserRepository repository;

    @Test void conservaUsuariosExistentesYAsignaValoresIniciales() {
        var usuario = repository.findByEmail("anterior@example.com").orElseThrow();
        assertEquals("Cliente", usuario.getNombre());
        assertEquals("hash-anterior", usuario.getPassword());
        assertEquals(100000L, usuario.getLineaCredito());
        assertEquals(0L, usuario.getSaldoUtilizado());
        assertEquals(0L, usuario.getVersionSesion());
        assertTrue(usuario.getActivo());
        assertFalse(usuario.getEliminado());
        assertNull(usuario.getRut());
    }
}
