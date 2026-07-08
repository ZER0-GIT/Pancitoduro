package cafe.pancitoduro.backend.service;

import cafe.pancitoduro.backend.dto.LoginRequest;
import cafe.pancitoduro.backend.dto.RegistroRequest;
import cafe.pancitoduro.backend.model.Rol;
import cafe.pancitoduro.backend.model.Usuario;
import cafe.pancitoduro.backend.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario registrar(RegistroRequest request) {

        String correo = request.getCorreo()
                .trim()
                .toLowerCase();

        if (usuarioRepository.existsByCorreo(correo)) {
            throw new IllegalArgumentException(
                    "El correo ya está registrado"
            );
        }

        Usuario usuario = new Usuario();

        usuario.setNombre(request.getNombre().trim());
        usuario.setApellido(request.getApellido().trim());
        usuario.setCorreo(correo);

        usuario.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        usuario.setTelefono(request.getTelefono());
        usuario.setRol(Rol.CLIENTE);

        return usuarioRepository.save(usuario);
    }
    public Usuario login(LoginRequest request) {

        String correo = request.getCorreo()
                .trim()
                .toLowerCase();

        Usuario usuario = usuarioRepository
                .findByCorreo(correo)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Correo o contraseña incorrectos"
                        )
                );

        boolean passwordCorrecto = passwordEncoder.matches(
                request.getPassword(),
                usuario.getPassword()
        );

        if (!passwordCorrecto) {
            throw new IllegalArgumentException(
                    "Correo o contraseña incorrectos"
            );
        }

        return usuario;
    }
    public Usuario buscarPorId(Long id) {

        return usuarioRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Usuario no encontrado"
                        )
                );
    }
}