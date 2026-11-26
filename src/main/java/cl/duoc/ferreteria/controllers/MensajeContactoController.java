package cl.duoc.ferreteria.controllers;

import cl.duoc.ferreteria.models.MensajeContacto;
import cl.duoc.ferreteria.services.MensajeContactoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mensajes")
public class MensajeContactoController {

    private final MensajeContactoService service;

    public MensajeContactoController(MensajeContactoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MensajeContacto> crearMensaje(
            @Valid @RequestBody MensajeContacto mensaje) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.crearMensaje(mensaje));
    }

    @GetMapping
    public List<MensajeContacto> listarMensajes() {
        return service.listarMensajes();
    }
}