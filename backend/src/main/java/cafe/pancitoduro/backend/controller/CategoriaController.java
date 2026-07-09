package cafe.pancitoduro.backend.controller;

import cafe.pancitoduro.backend.dto.CategoriaRequest;
import cafe.pancitoduro.backend.model.Categoria;
import cafe.pancitoduro.backend.service.CategoriaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> listar() {
        return ResponseEntity.ok(
                categoriaService.listar()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(
            @PathVariable Long id
    ) {
        try {
            return ResponseEntity.ok(
                    categoriaService.buscarPorId(id)
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensaje", e.getMessage()
                    ));
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(
            @Valid @RequestBody CategoriaRequest request
    ) {
        try {
            Categoria categoria =
                    categoriaService.crear(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(categoria);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "mensaje", e.getMessage()
                    ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequest request
    ) {
        try {
            return ResponseEntity.ok(
                    categoriaService.actualizar(id, request)
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensaje", e.getMessage()
                    ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable Long id
    ) {
        try {
            categoriaService.eliminar(id);

            return ResponseEntity.ok(
                    Map.of(
                            "mensaje",
                            "Categoría eliminada correctamente"
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensaje", e.getMessage()
                    ));
        }
    }
}