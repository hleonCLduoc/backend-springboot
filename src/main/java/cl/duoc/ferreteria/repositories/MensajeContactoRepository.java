package cl.duoc.ferreteria.repositories;

import cl.duoc.ferreteria.models.MensajeContacto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensajeContactoRepository
        extends JpaRepository<MensajeContacto, Long> {
}