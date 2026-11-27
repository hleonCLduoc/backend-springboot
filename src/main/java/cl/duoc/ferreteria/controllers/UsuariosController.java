package cl.duoc.ferreteria.controllers;

import cl.duoc.ferreteria.dtos.ClienteDTO;
import org.springframework.security.core.Authentication;
import cl.duoc.ferreteria.dtos.UserDTO;
import cl.duoc.ferreteria.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuariosController {

    private final UserService userService;

    public UsuariosController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/mi-cuenta")
    public UserDTO miCuenta(Authentication authentication) {
        return userService.miCuenta(authentication.getName());
    }

    @GetMapping
    public List<UserDTO> listarUsuarios() {
        return userService.listarUsuarios();
    }

    @GetMapping("/{id}")
    public UserDTO buscarUsuario(@PathVariable Long id) {
        return userService.buscarUsuario(id);
    }

    @PostMapping
    public ResponseEntity<UserDTO> crearUsuario(
            @Valid @RequestBody ClienteDTO datos) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.crearCliente(datos));
    }

    @PutMapping("/{id}")
    public UserDTO actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody ClienteDTO datos) {

        return userService.actualizarUsuario(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        userService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
    
}