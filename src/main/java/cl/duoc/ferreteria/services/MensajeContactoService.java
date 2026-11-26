package cl.duoc.ferreteria.services;

import cl.duoc.ferreteria.models.MensajeContacto;
import cl.duoc.ferreteria.repositories.MensajeContactoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MensajeContactoService {

    private final MensajeContactoRepository repository;

    public MensajeContactoService(MensajeContactoRepository repository) {
        this.repository = repository;
    }

    public MensajeContacto crearMensaje(MensajeContacto mensaje) {
        mensaje.setId(null);
        return repository.save(mensaje);
    }

    public List<MensajeContacto> listarMensajes() {
        return repository.findAll();
    }
}