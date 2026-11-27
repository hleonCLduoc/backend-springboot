package cl.duoc.ferreteria.dtos;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteDTO {
    @NotBlank @Size(max = 200)
    private String nombre;
    @NotBlank @Size(max = 200)
    private String apellido;
    @NotBlank @Email @Size(max = 254)
    private String email;
    @Size(max = 12)
    private String rut;
    @Size(min = 8, max = 72)
    private String password;
    @NotNull @Min(0) @Max(100000000)
    private Long lineaCredito;
    @NotNull
    private Boolean activo;
}
