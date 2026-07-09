package cafe.pancitoduro.backend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "ofertas_rescate",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_oferta_producto",
                        columnNames = "producto_id"
                )
        }
)
public class OfertaRescate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "producto_id",
            nullable = false,
            unique = true
    )
    private Producto producto;

    @Column(
            name = "precio_oferta",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal precioOferta;

    @Column(nullable = false)
    private Boolean activa = true;

    public OfertaRescate() {
    }

    @PrePersist
    public void prePersist() {
        if (activa == null) {
            activa = true;
        }
    }

    public Long getId() {
        return id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public BigDecimal getPrecioOferta() {
        return precioOferta;
    }

    public void setPrecioOferta(BigDecimal precioOferta) {
        this.precioOferta = precioOferta;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }
}