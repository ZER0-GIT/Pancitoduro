package cafe.pancitoduro.backend.service;

import cafe.pancitoduro.backend.dto.ProductoRequest;
import cafe.pancitoduro.backend.dto.ProductoResponse;
import cafe.pancitoduro.backend.model.Categoria;
import cafe.pancitoduro.backend.model.Producto;
import cafe.pancitoduro.backend.repository.CategoriaRepository;
import cafe.pancitoduro.backend.repository.ProductoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository
    ) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar() {

        return productoRepository
                .findByActivoTrue()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse buscarPorId(Long id) {

        Producto producto = productoRepository
                .findByIdAndActivoTrue(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Producto no encontrado"
                        )
                );

        return convertirAResponse(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarPorCategoria(
            Long categoriaId
    ) {

        if (!categoriaRepository.existsById(categoriaId)) {
            throw new IllegalArgumentException(
                    "Categoría no encontrada"
            );
        }

        return productoRepository
                .findByCategoriaIdAndActivoTrue(categoriaId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest request) {

        String nombre = request.getNombre().trim();

        if (productoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new IllegalArgumentException(
                    "Ya existe un producto con ese nombre"
            );
        }

        Categoria categoria = buscarCategoria(
                request.getCategoriaId()
        );

        Producto producto = new Producto();

        producto.setNombre(nombre);

        producto.setDescripcion(
                limpiarTexto(request.getDescripcion())
        );

        producto.setPrecio(request.getPrecio());

        producto.setStock(request.getStock());

        producto.setImagenKey(
                limpiarTexto(request.getImagenKey())
        );

        producto.setCategoria(categoria);

        Producto productoGuardado =
                productoRepository.save(producto);

        return convertirAResponse(productoGuardado);
    }

    @Transactional
    public ProductoResponse actualizar(
            Long id,
            ProductoRequest request
    ) {

        Producto producto = productoRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Producto no encontrado"
                        )
                );

        String nombre = request.getNombre().trim();

        productoRepository
                .findByNombreIgnoreCase(nombre)
                .filter(productoExistente ->
                        !productoExistente
                                .getId()
                                .equals(id)
                )
                .ifPresent(productoExistente -> {
                    throw new IllegalArgumentException(
                            "Ya existe un producto con ese nombre"
                    );
                });

        Categoria categoria = buscarCategoria(
                request.getCategoriaId()
        );

        producto.setNombre(nombre);

        producto.setDescripcion(
                limpiarTexto(request.getDescripcion())
        );

        producto.setPrecio(request.getPrecio());

        producto.setStock(request.getStock());

        producto.setImagenKey(
                limpiarTexto(request.getImagenKey())
        );

        producto.setCategoria(categoria);

        Producto productoActualizado =
                productoRepository.save(producto);

        return convertirAResponse(productoActualizado);
    }

    @Transactional
    public void eliminar(Long id) {

        Producto producto = productoRepository
                .findByIdAndActivoTrue(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Producto no encontrado"
                        )
                );

        producto.setActivo(false);

        productoRepository.save(producto);
    }

    private Categoria buscarCategoria(Long categoriaId) {

        return categoriaRepository
                .findById(categoriaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Categoría no encontrada"
                        )
                );
    }

    private ProductoResponse convertirAResponse(
            Producto producto
    ) {

        ProductoResponse response =
                new ProductoResponse();

        response.setId(producto.getId());

        response.setNombre(producto.getNombre());

        response.setDescripcion(
                producto.getDescripcion()
        );

        response.setPrecio(producto.getPrecio());

        response.setStock(producto.getStock());

        response.setImagenKey(
                producto.getImagenKey()
        );

        response.setActivo(producto.getActivo());

        response.setCategoriaId(
                producto.getCategoria().getId()
        );

        response.setCategoriaNombre(
                producto.getCategoria().getNombre()
        );

        response.setFechaCreacion(
                producto.getFechaCreacion()
        );

        return response;
    }

    private String limpiarTexto(String texto) {

        if (texto == null) {
            return null;
        }

        String resultado = texto.trim();

        return resultado.isEmpty()
                ? null
                : resultado;
    }
}