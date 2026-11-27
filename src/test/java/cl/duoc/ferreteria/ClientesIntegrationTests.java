package cl.duoc.ferreteria;

import cl.duoc.ferreteria.enums.Rol;
import cl.duoc.ferreteria.models.User;
import cl.duoc.ferreteria.repositories.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.net.URI;
import java.net.http.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = {
    "server.port=18083", "spring.profiles.active=test",
    "spring.datasource.url=jdbc:h2:mem:clientes_test;DB_CLOSE_DELAY=-1",
    "spring.datasource.username=sa", "spring.datasource.password=",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop", "spring.devtools.restart.enabled=false",
    "application.security.jwt.secret-key=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY="
})
class ClientesIntegrationTests {
    @Autowired UserRepository repository;
    @Autowired PasswordEncoder encoder;
    final HttpClient http = HttpClient.newHttpClient();
    final ObjectMapper json = new ObjectMapper();
    String adminToken;
    Long adminId;

    @BeforeEach
    void preparar() throws Exception {
        repository.deleteAll();
        User admin = new User();
        admin.setNombre("Admin"); admin.setApellido("Pruebas");
        admin.setEmail("admin@example.com"); admin.setPassword(encoder.encode("Admin1234"));
        admin.setRol(Rol.ADMIN); adminId = repository.saveAndFlush(admin).getId();
        adminToken = login("admin@example.com", "Admin1234");
    }

    Map<String, Object> datos() {
        return new HashMap<>(Map.of("nombre", "Ana", "apellido", "Soto", "email", "ana@example.com",
                "rut", "12.345.678-5", "password", "Prueba1234", "lineaCredito", 250000, "activo", true));
    }

    HttpResponse<String> pedir(String metodo, String ruta, Object datos, String token) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create("http://localhost:18083" + ruta))
                .header("Content-Type", "application/json");
        if (token != null) builder.header("Authorization", "Bearer " + token);
        builder.method(metodo, datos == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(json.writeValueAsString(datos)));
        return http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    String login(String email, String password) throws Exception {
        var respuesta = pedir("POST", "/api/v1/auth/iniciar-sesion", Map.of("email", email, "password", password), null);
        assertEquals(200, respuesta.statusCode(), respuesta.body());
        return json.readTree(respuesta.body()).path("datos").path("token").asText();
    }

    JsonNode crear() throws Exception {
        var respuesta = pedir("POST", "/api/v1/usuarios", datos(), adminToken);
        assertEquals(201, respuesta.statusCode(), respuesta.body());
        return json.readTree(respuesta.body());
    }

    @Test void registroPublicoImponeCupoRolYEstado() throws Exception {
        var entrada = datos(); entrada.put("rol", "ADMIN"); entrada.put("activo", false);
        var respuesta = pedir("POST", "/api/v1/auth/registrar", entrada, null);
        assertEquals(201, respuesta.statusCode());
        var usuario = json.readTree(respuesta.body()).path("datos");
        assertEquals("USER", usuario.path("rol").asText());
        assertEquals(100000, usuario.path("lineaCredito").asLong());
        assertTrue(usuario.path("activo").asBoolean());
        assertEquals("12345678-5", usuario.path("rut").asText());
        assertFalse(usuario.has("password"));
        assertTrue(encoder.matches("Prueba1234", repository.findByEmail("ana@example.com").orElseThrow().getPassword()));
    }

    @Test void validaRutYDuplicados() throws Exception {
        var entrada = datos(); entrada.put("rut", "12345678-0");
        assertEquals(400, pedir("POST", "/api/v1/auth/registrar", entrada, null).statusCode());
        crear();
        entrada = datos(); entrada.put("email", "otra@example.com");
        assertEquals(409, pedir("POST", "/api/v1/usuarios", entrada, adminToken).statusCode());
        assertEquals(409, pedir("POST", "/api/v1/auth/registrar", datos(), null).statusCode());
    }

    @Test void soloAdminGestionaClientes() throws Exception {
        crear(); String token = login("ana@example.com", "Prueba1234");
        assertEquals(401, pedir("GET", "/api/v1/usuarios", null, null).statusCode());
        assertEquals(403, pedir("GET", "/api/v1/usuarios", null, token).statusCode());
        assertEquals(403, pedir("POST", "/api/v1/usuarios", datos(), token).statusCode());
        assertEquals(200, pedir("GET", "/api/v1/usuarios/mi-cuenta", null, token).statusCode());
    }

    @Test void actualizaCupoSinPedirPasswordYRevocaCuentaDesactivada() throws Exception {
        long id = crear().path("id").asLong(); String token = login("ana@example.com", "Prueba1234");
        var cambios = datos(); cambios.remove("password"); cambios.put("lineaCredito", 300000);
        assertEquals(200, pedir("PUT", "/api/v1/usuarios/" + id, cambios, adminToken).statusCode());
        assertEquals(300000, repository.findById(id).orElseThrow().getLineaCredito());
        assertEquals(200, pedir("GET", "/api/v1/usuarios/mi-cuenta", null, token).statusCode());
        cambios.put("activo", false);
        assertEquals(200, pedir("PUT", "/api/v1/usuarios/" + id, cambios, adminToken).statusCode());
        assertEquals(401, pedir("GET", "/api/v1/usuarios/mi-cuenta", null, token).statusCode());
        assertEquals(401, pedir("POST", "/api/v1/auth/iniciar-sesion", datos(), null).statusCode());
        cambios.put("activo", true);
        assertEquals(200, pedir("PUT", "/api/v1/usuarios/" + id, cambios, adminToken).statusCode());
        assertEquals(401, pedir("GET", "/api/v1/usuarios/mi-cuenta", null, token).statusCode());
        login("ana@example.com", "Prueba1234");
    }

    @Test void noReduceCupoBajoSaldoNiAceptaCuposInvalidos() throws Exception {
        long id = crear().path("id").asLong();
        User usuario = repository.findById(id).orElseThrow(); usuario.setSaldoUtilizado(50000L); repository.saveAndFlush(usuario);
        var cambios = datos(); cambios.put("lineaCredito", 40000);
        assertEquals(409, pedir("PUT", "/api/v1/usuarios/" + id, cambios, adminToken).statusCode());
        assertEquals(409, pedir("DELETE", "/api/v1/usuarios/" + id, null, adminToken).statusCode());
        cambios.put("lineaCredito", -1);
        assertEquals(400, pedir("PUT", "/api/v1/usuarios/" + id, cambios, adminToken).statusCode());
        assertEquals(250000, repository.findById(id).orElseThrow().getLineaCredito());
    }

    @Test void eliminaSinBorrarHistorialYBloqueaToken() throws Exception {
        long id = crear().path("id").asLong(); String token = login("ana@example.com", "Prueba1234");
        assertEquals(204, pedir("DELETE", "/api/v1/usuarios/" + id, null, adminToken).statusCode());
        assertTrue(repository.findById(id).orElseThrow().getEliminado());
        assertEquals(404, pedir("GET", "/api/v1/usuarios/" + id, null, adminToken).statusCode());
        assertFalse(pedir("GET", "/api/v1/usuarios", null, adminToken).body().contains("ana@example.com"));
        assertEquals(401, pedir("GET", "/api/v1/usuarios/mi-cuenta", null, token).statusCode());
        assertEquals(403, pedir("DELETE", "/api/v1/usuarios/" + adminId, null, adminToken).statusCode());
    }

    @Test void cambiarPasswordInvalidaTokenAnterior() throws Exception {
        long id = crear().path("id").asLong(); String token = login("ana@example.com", "Prueba1234");
        var cambios = datos(); cambios.put("password", "NuevaClave123");
        assertEquals(200, pedir("PUT", "/api/v1/usuarios/" + id, cambios, adminToken).statusCode());
        assertEquals(401, pedir("GET", "/api/v1/usuarios/mi-cuenta", null, token).statusCode());
        login("ana@example.com", "NuevaClave123");
    }
}
