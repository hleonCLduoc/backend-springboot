package cl.duoc.ferreteria.models;

import cl.duoc.ferreteria.enums.Rol;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String nombre;

    private String apellido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(unique = true, length = 12)
    private String rut;

    @Column(nullable = false, columnDefinition = "bigint default 100000")
    private Long lineaCredito = 100000L;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long saldoUtilizado = 0L;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean activo = true;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean eliminado = false;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long versionSesion = 0L;
}