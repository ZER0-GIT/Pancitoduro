package cafe.pancitoduro.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ProductoRequest {

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(
            max = 100,
            message = "El nombre no puede superar los 100 caracteres"
    )
    private String nombre;

    @Size(
            max = 1000,
            message = "La descripción no puede superar los 1000 caracteres"
    )
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(
            value = "0.01",
            message = "El precio debe ser mayor que 0"
    )
    private BigDecimal precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(
            value = 0,
            message = "El stock no puede ser negativo"
    )
    private Integer stock;

    @Size(
            max = 500,
            message = "La clave de imagen no puede superar los 500 caracteres"
    )
    private String imagenKey;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getImagenKey() {
        return imagenKey;
    }

    public void setImagenKey(String imagenKey) {
        this.imagenKey = imagenKey;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }
}