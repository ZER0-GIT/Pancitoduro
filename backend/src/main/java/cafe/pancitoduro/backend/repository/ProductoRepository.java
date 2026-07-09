package cafe.pancitoduro.backend.repository;

import cafe.pancitoduro.backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrue();

    List<Producto> findByCategoriaIdAndActivoTrue(Long categoriaId);

    Optional<Producto> findByIdAndActivoTrue(Long id);

    boolean existsByNombreIgnoreCase(String nombre);
    
    Optional<Producto> findByNombreIgnoreCase(String nombre);
}