package cafe.pancitoduro.backend.controller;

import cafe.pancitoduro.backend.dto.CrearPedidoRequest;
import cafe.pancitoduro.backend.dto.PedidoResponse;
import cafe.pancitoduro.backend.service.PedidoService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<?> crearPedido(
            @Valid @RequestBody CrearPedidoRequest request,
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

            PedidoResponse pedido =
                    pedidoService.crearPedido(
                            usuarioId,
                            request
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(pedido);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensaje",
                            e.getMessage()
                    ));
        }
    }

    @GetMapping("/mis-pedidos")
    public ResponseEntity<?> listarMisPedidos(
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

        List<PedidoResponse> pedidos =
                pedidoService.listarMisPedidos(usuarioId);

        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/mis-pedidos/{id}")
    public ResponseEntity<?> buscarMiPedido(
            @PathVariable Long id,
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

            PedidoResponse pedido =
                    pedidoService.buscarMiPedido(
                            id,
                            usuarioId
                    );

            return ResponseEntity.ok(pedido);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensaje",
                            e.getMessage()
                    ));
        }
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listarTodos() {

        return ResponseEntity.ok(
                pedidoService.listarTodos()
        );
    }
}