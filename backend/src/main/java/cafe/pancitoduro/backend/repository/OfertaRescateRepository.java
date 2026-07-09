package cafe.pancitoduro.backend.repository;

import cafe.pancitoduro.backend.model.OfertaRescate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfertaRescateRepository
        extends JpaRepository<OfertaRescate, Long> {

    List<OfertaRescate> findByActivaTrue();

    Optional<OfertaRescate> findByIdAndActivaTrue(Long id);

    Optional<OfertaRescate> findByProductoId(Long productoId);
    
    Optional<OfertaRescate> findByProductoIdAndActivaTrue(Long productoId);

    boolean existsByProductoId(Long productoId);
}