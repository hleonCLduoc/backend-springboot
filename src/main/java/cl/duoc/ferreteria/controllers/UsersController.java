package cl.duoc.ferreteria.controllers;

import cl.duoc.ferreteria.dtos.ApiResponseDTO;
import cl.duoc.ferreteria.dtos.CredentialsDTO;
import cl.duoc.ferreteria.dtos.RegistroDTO;
import cl.duoc.ferreteria.dtos.UserDTO;
import cl.duoc.ferreteria.services.UserService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirements
@RequestMapping("/api/v1/auth")
public class UsersController {

    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<ApiResponseDTO<UserDTO>> registrar(
            @Valid @RequestBody RegistroDTO datos) {

        try {
            UserDTO usuario = userService.registrarUsuario(datos);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDTO.<UserDTO>builder()
                            .mensaje("Cuenta creada con éxito")
                            .datos(usuario)
                            .build());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponseDTO.<UserDTO>builder()
                            .mensaje(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/iniciar-sesion")
    public ResponseEntity<ApiResponseDTO<UserDTO>> iniciarSesion(
            @RequestBody CredentialsDTO credenciales) {

        try {
            UserDTO usuario = userService.iniciarSesion(
                    credenciales.getEmail(),
                    credenciales.getPassword()
            );

            return ResponseEntity.ok(
                    ApiResponseDTO.<UserDTO>builder()
                            .mensaje("Sesión iniciada con éxito")
                            .datos(usuario)
                            .build()
            );

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDTO.<UserDTO>builder()
                            .mensaje(e.getMessage())
                            .build());
        }
    }
}