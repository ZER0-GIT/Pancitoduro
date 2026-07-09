package cafe.pancitoduro.backend.service;

import cafe.pancitoduro.backend.dto.CategoriaRequest;
import cafe.pancitoduro.backend.model.Categoria;
import cafe.pancitoduro.backend.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Categoría no encontrada")
                );
    }

    public Categoria crear(CategoriaRequest request) {

        String nombre = request.getNombre().trim();

        if (categoriaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new IllegalArgumentException(
                    "Ya existe una categoría con ese nombre"
            );
        }

        Categoria categoria = new Categoria();

        categoria.setNombre(nombre);
        categoria.setDescripcion(
                request.getDescripcion() == null
                        ? null
                        : request.getDescripcion().trim()
        );

        return categoriaRepository.save(categoria);
    }

    public Categoria actualizar(
            Long id,
            CategoriaRequest request
    ) {

        Categoria categoria = buscarPorId(id);

        String nombre = request.getNombre().trim();

        categoriaRepository
                .findByNombreIgnoreCase(nombre)
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> {
                    throw new IllegalArgumentException(
                            "Ya existe una categoría con ese nombre"
                    );
                });

        categoria.setNombre(nombre);

        categoria.setDescripcion(
                request.getDescripcion() == null
                        ? null
                        : request.getDescripcion().trim()
        );

        return categoriaRepository.save(categoria);
    }

    public void eliminar(Long id) {

        Categoria categoria = buscarPorId(id);

        categoriaRepository.delete(categoria);
    }
}