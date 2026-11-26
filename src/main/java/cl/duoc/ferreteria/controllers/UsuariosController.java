package cl.duoc.ferreteria.controllers;

import cl.duoc.ferreteria.dtos.RegistroDTO;
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
            @Valid @RequestBody RegistroDTO datos) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.registrarUsuario(datos));
    }

    @PutMapping("/{id}")
    public UserDTO actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody RegistroDTO datos) {

        return userService.actualizarUsuario(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        userService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
    
}