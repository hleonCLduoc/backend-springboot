package cl.duoc.ferreteria.dtos;

import cl.duoc.ferreteria.enums.Rol;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDTO {

    private Long id;
    private String email;
    private String nombre;
    private String apellido;
    private Rol rol;
    private String token;
}