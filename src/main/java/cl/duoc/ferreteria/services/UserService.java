package cl.duoc.ferreteria.services;

import cl.duoc.ferreteria.dtos.RegistroDTO;
import cl.duoc.ferreteria.dtos.UserDTO;
import cl.duoc.ferreteria.enums.Rol;
import cl.duoc.ferreteria.models.User;
import cl.duoc.ferreteria.repositories.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            UserDetailsService userDetailsService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }
    public UserDTO buscarUsuario(Long id) {
        User usuario = obtenerUsuario(id);
        return crearRespuesta(usuario, null);
    }

    public UserDTO actualizarUsuario(Long id, RegistroDTO datos) {
        User usuario = obtenerUsuario(id);
        String email = datos.getEmail().trim().toLowerCase(Locale.ROOT);

        userRepository.findByEmail(email).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "El correo ya está registrado"
                );            }
        });

        usuario.setNombre(datos.getNombre().trim());
        usuario.setApellido(datos.getApellido().trim());
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(datos.getPassword()));

        return crearRespuesta(userRepository.save(usuario), null);
    }

    public void eliminarUsuario(Long id) {
        User usuario = obtenerUsuario(id);
        userRepository.delete(usuario);
    }

    private User obtenerUsuario(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Usuario no encontrado"
                        )
                );
    }
    public List<UserDTO> listarUsuarios() {
        return userRepository.findAll()
                .stream()
                .map(usuario -> crearRespuesta(usuario, null))
                .toList();
    }

    public UserDTO registrarUsuario(RegistroDTO datos) {
        String email = datos.getEmail().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El correo ya está registrado"
            );        }

        User usuario = new User();
        usuario.setNombre(datos.getNombre().trim());
        usuario.setApellido(datos.getApellido().trim());
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(datos.getPassword()));
        usuario.setRol(Rol.USER);

        User guardado = userRepository.save(usuario);

        return crearRespuesta(guardado, null);
    }

    public UserDTO iniciarSesion(String correo, String password) {
        if (correo == null || password == null) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        String email = correo.trim().toLowerCase(Locale.ROOT);

        User usuario = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new BadCredentialsException("Credenciales inválidas")
                );

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(email);

        String token = jwtService.generateToken(userDetails);

        return crearRespuesta(usuario, token);
    }

    private UserDTO crearRespuesta(User usuario, String token) {
        return UserDTO.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .rol(usuario.getRol())
                .token(token)
                .build();
    }
}