package cl.duoc.ferreteria.services;

import cl.duoc.ferreteria.dtos.*;
import cl.duoc.ferreteria.enums.Rol;
import cl.duoc.ferreteria.models.User;
import cl.duoc.ferreteria.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    public List<UserDTO> listarUsuarios() {
        return userRepository.findByEliminadoFalseOrderByIdAsc().stream()
                .map(u -> crearRespuesta(u, null)).toList();
    }

    public UserDTO buscarUsuario(Long id) {
        return crearRespuesta(obtenerUsuario(id), null);
    }

    public UserDTO miCuenta(String email) {
        User usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Sesión inválida"));
        return crearRespuesta(usuario, null);
    }

    public UserDTO registrarUsuario(RegistroDTO datos) {
        User usuario = nuevoUsuario(datos.getNombre(), datos.getApellido(), datos.getEmail(),
                datos.getRut(), datos.getPassword());
        return crearRespuesta(userRepository.save(usuario), null);
    }

    public UserDTO crearCliente(ClienteDTO datos) {
        validarPassword(datos.getPassword());
        User usuario = nuevoUsuario(datos.getNombre(), datos.getApellido(), datos.getEmail(),
                datos.getRut(), datos.getPassword());
        usuario.setLineaCredito(datos.getLineaCredito());
        usuario.setActivo(datos.getActivo());
        return crearRespuesta(userRepository.save(usuario), null);
    }

    public UserDTO actualizarUsuario(Long id, ClienteDTO datos) {
        User usuario = obtenerCliente(id);
        String email = datos.getEmail().trim().toLowerCase(Locale.ROOT);
        String rut = datos.getRut() == null || datos.getRut().isBlank() ? usuario.getRut() : Rut.normalizar(datos.getRut());
        validarDuplicados(email, rut, id);
        if (datos.getLineaCredito() < usuario.getSaldoUtilizado()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El cupo no puede ser menor al saldo utilizado");
        }
        boolean cambiaSesion = !usuario.getEmail().equals(email)
                || !usuario.getActivo().equals(datos.getActivo());
        if (datos.getPassword() != null && !datos.getPassword().isBlank()) {
            validarPassword(datos.getPassword());
            usuario.setPassword(passwordEncoder.encode(datos.getPassword()));
            cambiaSesion = true;
        }
        usuario.setNombre(datos.getNombre().trim());
        usuario.setApellido(datos.getApellido().trim());
        usuario.setEmail(email);
        usuario.setRut(rut);
        usuario.setLineaCredito(datos.getLineaCredito());
        usuario.setActivo(datos.getActivo());
        if (cambiaSesion) usuario.setVersionSesion(usuario.getVersionSesion() + 1);
        return crearRespuesta(userRepository.save(usuario), null);
    }

    public void eliminarUsuario(Long id) {
        User usuario = obtenerCliente(id);
        if (usuario.getSaldoUtilizado() > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No puedes eliminar una cuenta con saldo pendiente");
        }
        usuario.setEliminado(true);
        usuario.setActivo(false);
        usuario.setVersionSesion(usuario.getVersionSesion() + 1);
        userRepository.save(usuario);
    }

    public UserDTO iniciarSesion(String correo, String password) {
        if (correo == null || password == null) throw new BadCredentialsException("Credenciales inválidas");
        User usuario = userRepository.findByEmail(correo.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        if (!usuario.getActivo() || usuario.getEliminado()) {
            throw new BadCredentialsException("Tu cuenta está desactivada. Contacta al administrador.");
        }
        String token = jwtService.generateToken(userDetailsService.loadUserByUsername(usuario.getEmail()));
        return crearRespuesta(usuario, token);
    }

    private User nuevoUsuario(String nombre, String apellido, String correo, String rutEntrada, String password) {
        validarPassword(password);
        String email = correo.trim().toLowerCase(Locale.ROOT);
        String rut = Rut.normalizar(rutEntrada);
        validarDuplicados(email, rut, null);
        User usuario = new User();
        usuario.setNombre(nombre.trim());
        usuario.setApellido(apellido.trim());
        usuario.setEmail(email);
        usuario.setRut(rut);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(Rol.USER);
        return usuario;
    }

    private void validarDuplicados(String email, String rut, Long id) {
        userRepository.findByEmail(email).ifPresent(u -> {
            if (!u.getId().equals(id)) throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        });
        if (rut != null) userRepository.findByRut(rut).ifPresent(u -> {
            if (!u.getId().equals(id)) throw new ResponseStatusException(HttpStatus.CONFLICT, "El RUT ya está registrado");
        });
    }

    private void validarPassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 72
                || password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener entre 8 y 72 caracteres y no superar 72 bytes");
        }
    }

    private User obtenerUsuario(Long id) {
        return userRepository.findById(id).filter(u -> !u.getEliminado())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private User obtenerCliente(Long id) {
        User usuario = obtenerUsuario(id);
        if (usuario.getRol() != Rol.USER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo se gestionan cuentas de clientes");
        }
        return usuario;
    }

    private UserDTO crearRespuesta(User usuario, String token) {
        return UserDTO.builder().id(usuario.getId()).email(usuario.getEmail())
                .nombre(usuario.getNombre()).apellido(usuario.getApellido()).rol(usuario.getRol())
                .rut(usuario.getRut()).lineaCredito(usuario.getLineaCredito())
                .saldoUtilizado(usuario.getSaldoUtilizado()).activo(usuario.getActivo()).token(token).build();
    }
}
