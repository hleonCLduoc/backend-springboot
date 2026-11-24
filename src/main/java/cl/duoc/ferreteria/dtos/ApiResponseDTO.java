package cl.duoc.ferreteria.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponseDTO<T> {

    private String mensaje;
    private T datos;
}