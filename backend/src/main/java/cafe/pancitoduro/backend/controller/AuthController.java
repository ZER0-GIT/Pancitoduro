package cafe.pancitoduro.backend.controller;

import cafe.pancitoduro.backend.dto.LoginRequest;
import cafe.pancitoduro.backend.dto.RegistroRequest;
import cafe.pancitoduro.backend.model.Usuario;
import cafe.pancitoduro.backend.service.AuthService;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(
            @Valid @RequestBody RegistroRequest request
    ) {
        try {
            Usuario usuario = authService.registrar(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "mensaje",
                            "Usuario registrado correctamente",

                            "usuario",
                            construirUsuarioResponse(usuario)
                    ));

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "mensaje",
                            e.getMessage()
                    ));
        }
    }

        @PostMapping("/login")
        public ResponseEntity<?> login(
                @Valid @RequestBody LoginRequest request,
                HttpServletRequest servletRequest
        ) {

        try {

                Usuario usuario =
                        authService.login(request);

                /*
                * CREA EXPLÍCITAMENTE UNA SESIÓN
                */

                HttpSession session =
                        servletRequest.getSession(true);

                session.setAttribute(
                        "usuarioId",
                        usuario.getId()
                );

                System.out.println(
                        "LOGIN CORRECTO"
                );

                System.out.println(
                        "SESSION ID: "
                                + session.getId()
                );

                System.out.println(
                        "USUARIO ID EN SESION: "
                                + session.getAttribute("usuarioId")
                );


                return ResponseEntity.ok(
                        Map.of(
                                "mensaje",
                                "Sesión iniciada correctamente",

                                "sessionId",
                                session.getId(),

                                "usuario",
                                Map.of(
                                        "id",
                                        usuario.getId(),
                                        "nombre",
                                        usuario.getNombre(),
                                        "apellido",
                                        usuario.getApellido(),
                                        "correo",
                                        usuario.getCorreo(),
                                        "rol",
                                        usuario.getRol()
                                )
                        )
                );

        } catch (IllegalArgumentException e) {

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(
                                Map.of(
                                        "mensaje",
                                        e.getMessage()
                                )
                        );
        }
        }

    @GetMapping("/test")
    public String test() {
        return "AuthController funcionando";
    }

    @GetMapping("/me")
    public ResponseEntity<?> obtenerUsuarioActual(
            HttpSession session
    ) {
        Long usuarioId =
                (Long) session.getAttribute("usuarioId");

        if (usuarioId == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensaje",
                            "No hay una sesión activa"
                    ));
        }

        try {
            Usuario usuario =
                    authService.buscarPorId(usuarioId);

            return ResponseEntity.ok(
                    Map.of(
                            "usuario",
                            construirUsuarioResponse(usuario)
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensaje",
                            e.getMessage()
                    ));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpServletRequest request
    ) {
        HttpSession session =
                request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(
                Map.of(
                        "mensaje",
                        "Sesión cerrada correctamente"
                )
        );
    }

    private Map<String, Object> construirUsuarioResponse(
            Usuario usuario
    ) {
        return Map.of(
                "id",
                usuario.getId(),

                "nombre",
                usuario.getNombre(),

                "apellido",
                usuario.getApellido(),

                "correo",
                usuario.getCorreo(),

                "telefono",
                usuario.getTelefono() == null
                        ? ""
                        : usuario.getTelefono(),

                "rol",
                usuario.getRol().name()
        );
    }
}