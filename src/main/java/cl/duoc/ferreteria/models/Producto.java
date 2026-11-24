package cl.duoc.ferreteria.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Producto {

    @Id
    private String id;

    private String nombre;
    private String categoria;
    private Integer precio;
    private Integer stock;
    private String imagen;
    private Boolean disponible;
}
