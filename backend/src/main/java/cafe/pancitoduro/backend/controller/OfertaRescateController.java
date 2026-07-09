package cafe.pancitoduro.backend.controller;

import cafe.pancitoduro.backend.dto.OfertaRescateRequest;
import cafe.pancitoduro.backend.dto.OfertaRescateResponse;
import cafe.pancitoduro.backend.service.OfertaRescateService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ofertas")
public class OfertaRescateController {

    private final OfertaRescateService ofertaService;

    public OfertaRescateController(
            OfertaRescateService ofertaService
    ) {
        this.ofertaService = ofertaService;
    }

    @GetMapping
    public ResponseEntity<List<OfertaRescateResponse>> listar() {

        return ResponseEntity.ok(
                ofertaService.listar()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(
            @PathVariable Long id
    ) {

        try {

            return ResponseEntity.ok(
                    ofertaService.buscarPorId(id)
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
            @Valid @RequestBody OfertaRescateRequest request
    ) {

        try {

            OfertaRescateResponse oferta =
                    ofertaService.crear(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(oferta);

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
            @Valid @RequestBody OfertaRescateRequest request
    ) {

        try {

            return ResponseEntity.ok(
                    ofertaService.actualizar(id, request)
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
    public ResponseEntity<?> desactivar(
            @PathVariable Long id
    ) {

        try {

            ofertaService.desactivar(id);

            return ResponseEntity.ok(
                    Map.of(
                            "mensaje",
                            "Oferta desactivada correctamente"
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