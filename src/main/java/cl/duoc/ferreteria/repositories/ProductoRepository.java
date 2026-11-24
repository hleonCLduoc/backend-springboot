package cl.duoc.ferreteria.repositories;

import cl.duoc.ferreteria.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository
        extends JpaRepository<Producto, String> {
}