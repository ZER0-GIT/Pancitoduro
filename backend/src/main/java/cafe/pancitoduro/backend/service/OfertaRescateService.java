package cafe.pancitoduro.backend.service;

import cafe.pancitoduro.backend.dto.OfertaRescateRequest;
import cafe.pancitoduro.backend.dto.OfertaRescateResponse;
import cafe.pancitoduro.backend.model.OfertaRescate;
import cafe.pancitoduro.backend.model.Producto;
import cafe.pancitoduro.backend.repository.OfertaRescateRepository;
import cafe.pancitoduro.backend.repository.ProductoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OfertaRescateService {

    private final OfertaRescateRepository ofertaRepository;
    private final ProductoRepository productoRepository;

    public OfertaRescateService(
            OfertaRescateRepository ofertaRepository,
            ProductoRepository productoRepository
    ) {
        this.ofertaRepository = ofertaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<OfertaRescateResponse> listar() {

        return ofertaRepository
                .findByActivaTrue()
                .stream()
                .filter(oferta ->
                        Boolean.TRUE.equals(
                                oferta.getProducto().getActivo()
                        )
                )
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OfertaRescateResponse buscarPorId(Long id) {

        OfertaRescate oferta = ofertaRepository
                .findByIdAndActivaTrue(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Oferta de rescate no encontrada"
                        )
                );

        if (!Boolean.TRUE.equals(
                oferta.getProducto().getActivo()
        )) {
            throw new IllegalArgumentException(
                    "El producto de la oferta no está activo"
            );
        }

        return convertirAResponse(oferta);
    }

    @Transactional
    public OfertaRescateResponse crear(
            OfertaRescateRequest request
    ) {

        Producto producto = productoRepository
                .findByIdAndActivoTrue(request.getProductoId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Producto no encontrado"
                        )
                );

        validarPrecio(
                request.getPrecioOferta(),
                producto.getPrecio()
        );

        if (ofertaRepository.existsByProductoId(producto.getId())) {
            throw new IllegalArgumentException(
                    "El producto ya tiene una oferta de rescate"
            );
        }

        OfertaRescate oferta = new OfertaRescate();

        oferta.setProducto(producto);

        oferta.setPrecioOferta(
                request.getPrecioOferta()
        );

        oferta.setActiva(true);

        OfertaRescate ofertaGuardada =
                ofertaRepository.save(oferta);

        return convertirAResponse(ofertaGuardada);
    }

    @Transactional
    public OfertaRescateResponse actualizar(
            Long id,
            OfertaRescateRequest request
    ) {

        OfertaRescate oferta = ofertaRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Oferta de rescate no encontrada"
                        )
                );

        Producto producto = productoRepository
                .findByIdAndActivoTrue(request.getProductoId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Producto no encontrado"
                        )
                );

        ofertaRepository
                .findByProductoId(producto.getId())
                .filter(ofertaExistente ->
                        !ofertaExistente.getId().equals(id)
                )
                .ifPresent(ofertaExistente -> {
                    throw new IllegalArgumentException(
                            "El producto ya tiene una oferta de rescate"
                    );
                });

        validarPrecio(
                request.getPrecioOferta(),
                producto.getPrecio()
        );

        oferta.setProducto(producto);

        oferta.setPrecioOferta(
                request.getPrecioOferta()
        );

        oferta.setActiva(true);

        OfertaRescate ofertaActualizada =
                ofertaRepository.save(oferta);

        return convertirAResponse(ofertaActualizada);
    }

    @Transactional
    public void desactivar(Long id) {

        OfertaRescate oferta = ofertaRepository
                .findByIdAndActivaTrue(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Oferta de rescate no encontrada"
                        )
                );

        oferta.setActiva(false);

        ofertaRepository.save(oferta);
    }

    private void validarPrecio(
            BigDecimal precioOferta,
            BigDecimal precioNormal
    ) {

        if (precioOferta.compareTo(precioNormal) >= 0) {
            throw new IllegalArgumentException(
                    "El precio de oferta debe ser menor que el precio normal"
            );
        }
    }

    private OfertaRescateResponse convertirAResponse(
            OfertaRescate oferta
    ) {

        Producto producto = oferta.getProducto();

        OfertaRescateResponse response =
                new OfertaRescateResponse();

        response.setId(oferta.getId());

        response.setProductoId(producto.getId());

        response.setNombre(producto.getNombre());

        response.setDescripcion(producto.getDescripcion());

        response.setPrecioOriginal(producto.getPrecio());

        response.setPrecioOferta(oferta.getPrecioOferta());

        response.setStock(producto.getStock());

        response.setImagenKey(producto.getImagenKey());

        response.setActiva(oferta.getActiva());

        response.setCategoriaId(
                producto.getCategoria().getId()
        );

        response.setCategoriaNombre(
                producto.getCategoria().getNombre()
        );

        return response;
    }
}