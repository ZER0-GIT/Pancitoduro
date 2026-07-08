package cafe.pancitoduro.backend.controller;

import cafe.pancitoduro.backend.dto.RegistroRequest;
import cafe.pancitoduro.backend.model.Usuario;
import cafe.pancitoduro.backend.service.AuthService;
import cafe.pancitoduro.backend.dto.LoginRequest;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        System.out.println(">>> AUTH CONTROLLER CARGADO <<<");
        this.authService = authService;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(
            @Valid @RequestBody RegistroRequest request) {

        try {

            Usuario usuario = authService.registrar(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "mensaje", "Usuario registrado correctamente",
                            "id", usuario.getId()
                    ));

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "mensaje", e.getMessage()
                    ));
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session) {

        try {
            Usuario usuario = authService.login(request);

            session.setAttribute("usuarioId", usuario.getId());

            return ResponseEntity.ok(
                    Map.of(
                            "mensaje", "Sesión iniciada correctamente",
                            "usuario", Map.of(
                                    "id", usuario.getId(),
                                    "nombre", usuario.getNombre(),
                                    "apellido", usuario.getApellido(),
                                    "correo", usuario.getCorreo(),
                                    "rol", usuario.getRol()
                            )
                    )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensaje", e.getMessage()
                    ));
        }
    }
    @GetMapping("/test")
    public String test() {
        return "AuthController funcionando";
    }
    @GetMapping("/me")
    public ResponseEntity<?> obtenerUsuarioActual(
            HttpSession session) {

        Long usuarioId =
                (Long) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensaje", "No hay una sesión activa"
                    ));
        }

        Usuario usuario = authService.buscarPorId(usuarioId);

        return ResponseEntity.ok(
                Map.of(
                        "id", usuario.getId(),
                        "nombre", usuario.getNombre(),
                        "apellido", usuario.getApellido(),
                        "correo", usuario.getCorreo(),
                        "telefono",
                        usuario.getTelefono() == null
                                ? ""
                                : usuario.getTelefono(),
                        "rol", usuario.getRol()
                )
        );
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {

        session.invalidate();

        return ResponseEntity.ok(
                Map.of(
                        "mensaje", "Sesión cerrada correctamente"
                )
        );
    }
}
