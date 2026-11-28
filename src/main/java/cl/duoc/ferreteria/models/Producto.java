package cl.duoc.ferreteria.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Producto {

    @Id
    @NotBlank
    @Size(max = 30)
    private String id;

    @NotBlank
    @Size(max = 200)
    private String nombre;
    @NotBlank
    @Size(max = 200)
    private String categoria;

    private String marca;
    private String subcategoria;
    @NotNull
    @PositiveOrZero
    private Integer precio;
    @NotNull
    @PositiveOrZero
    private Integer stock;
    private String imagen;
    @NotNull
    private Boolean disponible;
}
