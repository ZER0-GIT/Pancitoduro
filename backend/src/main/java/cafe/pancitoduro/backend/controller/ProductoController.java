package cafe.pancitoduro.backend.controller;

import cafe.pancitoduro.backend.dto.ProductoRequest;
import cafe.pancitoduro.backend.dto.ProductoResponse;
import cafe.pancitoduro.backend.service.ProductoService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(
            ProductoService productoService
    ) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar() {

        return ResponseEntity.ok(
                productoService.listar()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(
            @PathVariable Long id
    ) {

        try {

            return ResponseEntity.ok(
                    productoService.buscarPorId(id)
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensaje",
                            e.getMessage()
                    ));
        }
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<?> listarPorCategoria(
            @PathVariable Long categoriaId
    ) {

        try {

            return ResponseEntity.ok(
                    productoService.listarPorCategoria(
                            categoriaId
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensaje",
                            e.getMessage()
                    ));
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(
            @Valid @RequestBody ProductoRequest request
    ) {

        try {

            ProductoResponse producto =
                    productoService.crear(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(producto);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "mensaje",
                            e.getMessage()
                    ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request
    ) {

        try {

            return ResponseEntity.ok(
                    productoService.actualizar(
                            id,
                            request
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensaje",
                            e.getMessage()
                    ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable Long id
    ) {

        try {

            productoService.eliminar(id);

            return ResponseEntity.ok(
                    Map.of(
                            "mensaje",
                            "Producto desactivado correctamente"
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensaje",
                            e.getMessage()
                    ));
        }
    }
}