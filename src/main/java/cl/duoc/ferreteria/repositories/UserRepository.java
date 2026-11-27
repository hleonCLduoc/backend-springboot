package cl.duoc.ferreteria.repositories;

import cl.duoc.ferreteria.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    Optional<User> findByRut(String rut);
    List<User> findByEliminadoFalseOrderByIdAsc();
}