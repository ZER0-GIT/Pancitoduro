package cafe.pancitoduro.backend.service;

import cafe.pancitoduro.backend.dto.CrearPedidoRequest;
import cafe.pancitoduro.backend.dto.DetallePedidoRequest;
import cafe.pancitoduro.backend.dto.DetallePedidoResponse;
import cafe.pancitoduro.backend.dto.PedidoResponse;

import cafe.pancitoduro.backend.model.DetallePedido;
import cafe.pancitoduro.backend.model.EstadoPago;
import cafe.pancitoduro.backend.model.EstadoPedido;
import cafe.pancitoduro.backend.model.OfertaRescate;
import cafe.pancitoduro.backend.model.Pedido;
import cafe.pancitoduro.backend.model.Producto;
import cafe.pancitoduro.backend.model.Usuario;

import cafe.pancitoduro.backend.repository.DetallePedidoRepository;
import cafe.pancitoduro.backend.repository.OfertaRescateRepository;
import cafe.pancitoduro.backend.repository.PedidoRepository;
import cafe.pancitoduro.backend.repository.ProductoRepository;
import cafe.pancitoduro.backend.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    private final DetallePedidoRepository detallePedidoRepository;

    private final ProductoRepository productoRepository;

    private final OfertaRescateRepository ofertaRescateRepository;

    private final UsuarioRepository usuarioRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            DetallePedidoRepository detallePedidoRepository,
            ProductoRepository productoRepository,
            OfertaRescateRepository ofertaRescateRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
        this.ofertaRescateRepository = ofertaRescateRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public PedidoResponse crearPedido(
            Long usuarioId,
            CrearPedidoRequest request
    ) {

        /*
         * 1. BUSCAR USUARIO
         */

        Usuario usuario = usuarioRepository
                .findById(usuarioId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Usuario no encontrado"
                        )
                );


        /*
         * 2. CREAR PEDIDO
         */

        Pedido pedido = new Pedido();

        pedido.setUsuario(usuario);

        pedido.setEstado(
                EstadoPedido.PENDIENTE
        );

        /*
         * Como el pago es simulado,
         * consideramos el pedido pagado.
         */

        pedido.setEstadoPago(
                EstadoPago.PAGADO
        );

        pedido.setMetodoPago(
                request.getMetodoPago()
        );

        pedido.setDireccionEntrega(
                limpiarTexto(
                        request.getDireccionEntrega()
                )
        );

        pedido.setTelefonoContacto(
                limpiarTexto(
                        request.getTelefonoContacto()
                )
        );

        pedido.setObservaciones(
                limpiarTexto(
                        request.getObservaciones()
                )
        );


        /*
         * IMPORTANTE:
         *
         * Las columnas subtotal y total
         * tienen NOT NULL en PostgreSQL.
         *
         * Por eso debemos inicializarlas
         * antes del primer INSERT.
         */

        pedido.setSubtotal(
                BigDecimal.ZERO
        );

        pedido.setTotal(
                BigDecimal.ZERO
        );


        /*
         * VARIABLE PARA CALCULAR EL TOTAL
         */

        BigDecimal totalPedido =
                BigDecimal.ZERO;


        /*
         * 3. GUARDAR PEDIDO
         *
         * Necesitamos obtener el ID
         * para relacionar los detalles.
         */

        Pedido pedidoGuardado =
                pedidoRepository.save(pedido);


        /*
         * LISTA DE DETALLES GUARDADOS
         */

        List<DetallePedido> detallesGuardados =
                new ArrayList<>();


        /*
         * 4. PROCESAR PRODUCTOS
         */

        for (
                DetallePedidoRequest detalleRequest
                : request.getDetalles()
        ) {

            /*
             * BUSCAR PRODUCTO ACTIVO
             */

            Producto producto =
                    productoRepository
                            .findByIdAndActivoTrue(
                                    detalleRequest.getProductoId()
                            )
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Producto no encontrado: "
                                                    + detalleRequest.getProductoId()
                                    )
                            );


            /*
             * OBTENER CANTIDAD
             */

            Integer cantidad =
                    detalleRequest.getCantidad();


            /*
             * VALIDAR STOCK
             */

            if (producto.getStock() < cantidad) {

                throw new IllegalArgumentException(
                        "Stock insuficiente para el producto: "
                                + producto.getNombre()
                );
            }


            /*
             * 5. BUSCAR OFERTA ACTIVA
             */

            Optional<OfertaRescate> ofertaOptional =
                    ofertaRescateRepository
                            .findByProductoIdAndActivaTrue(
                                    producto.getId()
                            );


            /*
             * PRECIO NORMAL POR DEFECTO
             */

            BigDecimal precioUnitario =
                    producto.getPrecio();


            /*
             * OFERTA APLICADA
             */

            OfertaRescate ofertaAplicada =
                    null;


            /*
             * SI EXISTE OFERTA ACTIVA
             * UTILIZAMOS SU PRECIO
             */

            if (ofertaOptional.isPresent()) {

                ofertaAplicada =
                        ofertaOptional.get();

                precioUnitario =
                        ofertaAplicada.getPrecioOferta();
            }


            /*
             * 6. CALCULAR SUBTOTAL
             */

            BigDecimal subtotalDetalle =
                    precioUnitario.multiply(
                            BigDecimal.valueOf(cantidad)
                    );


            /*
             * 7. CREAR DETALLE
             */

            DetallePedido detalle =
                    new DetallePedido();

            detalle.setPedido(
                    pedidoGuardado
            );

            detalle.setProducto(
                    producto
            );

            detalle.setOfertaRescate(
                    ofertaAplicada
            );

            detalle.setCantidad(
                    cantidad
            );

            detalle.setPrecioUnitario(
                    precioUnitario
            );

            detalle.setSubtotal(
                    subtotalDetalle
            );


            /*
             * 8. GUARDAR DETALLE
             */

            DetallePedido detalleGuardado =
                    detallePedidoRepository.save(detalle);

            detallesGuardados.add(
                    detalleGuardado
            );


            /*
             * 9. DESCONTAR STOCK
             */

            producto.setStock(
                    producto.getStock()
                            - cantidad
            );

            productoRepository.save(
                    producto
            );


            /*
             * 10. ACUMULAR TOTAL
             */

            totalPedido =
                    totalPedido.add(
                            subtotalDetalle
                    );
        }


        /*
         * 11. ACTUALIZAR TOTALES DEL PEDIDO
         */

        pedidoGuardado.setSubtotal(
                totalPedido
        );

        pedidoGuardado.setTotal(
                totalPedido
        );


        /*
         * 12. GUARDAR PEDIDO ACTUALIZADO
         */

        pedidoGuardado =
                pedidoRepository.save(
                        pedidoGuardado
                );


        /*
         * 13. DEVOLVER RESPONSE
         */

        return convertirAResponse(
                pedidoGuardado,
                detallesGuardados
        );
    }


    /*
     * LISTAR PEDIDOS DEL USUARIO
     */

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarMisPedidos(
            Long usuarioId
    ) {

        return pedidoRepository
                .findByUsuarioIdOrderByFechaPedidoDesc(
                        usuarioId
                )
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }


    /*
     * BUSCAR PEDIDO DEL USUARIO
     */

    @Transactional(readOnly = true)
    public PedidoResponse buscarMiPedido(
            Long pedidoId,
            Long usuarioId
    ) {

        Pedido pedido =
                pedidoRepository
                        .findById(pedidoId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Pedido no encontrado"
                                )
                        );


        /*
         * VALIDAR QUE EL PEDIDO
         * PERTENEZCA AL USUARIO
         */

        if (!pedido
                .getUsuario()
                .getId()
                .equals(usuarioId)) {

            throw new IllegalArgumentException(
                    "No tienes permiso para consultar este pedido"
            );
        }

        return convertirAResponse(
                pedido
        );
    }


    /*
     * LISTAR TODOS LOS PEDIDOS
     *
     * USADO POR ADMIN
     */

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarTodos() {

        return pedidoRepository
                .findAllByOrderByFechaPedidoDesc()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }


    /*
     * CONVERTIR PEDIDO A RESPONSE
     */

    private PedidoResponse convertirAResponse(
            Pedido pedido
    ) {

        List<DetallePedido> detalles =
                detallePedidoRepository
                        .findByPedidoId(
                                pedido.getId()
                        );

        return convertirAResponse(
                pedido,
                detalles
        );
    }


    /*
     * CONVERTIR PEDIDO Y DETALLES
     * A RESPONSE
     */

    private PedidoResponse convertirAResponse(
            Pedido pedido,
            List<DetallePedido> detalles
    ) {

        PedidoResponse response =
                new PedidoResponse();


        response.setId(
                pedido.getId()
        );


        response.setUsuarioId(
                pedido.getUsuario().getId()
        );


        response.setUsuarioNombre(
                pedido.getUsuario().getNombre()
                        + " "
                        + pedido.getUsuario().getApellido()
        );


        response.setFechaPedido(
                pedido.getFechaPedido()
        );


        response.setEstado(
                pedido.getEstado()
        );


        response.setEstadoPago(
                pedido.getEstadoPago()
        );


        response.setMetodoPago(
                pedido.getMetodoPago()
        );


        response.setSubtotal(
                pedido.getSubtotal()
        );


        response.setTotal(
                pedido.getTotal()
        );


        response.setDireccionEntrega(
                pedido.getDireccionEntrega()
        );


        response.setTelefonoContacto(
                pedido.getTelefonoContacto()
        );


        response.setObservaciones(
                pedido.getObservaciones()
        );


        List<DetallePedidoResponse> detallesResponse =
                detalles
                        .stream()
                        .map(
                                this::convertirDetalleAResponse
                        )
                        .toList();


        response.setDetalles(
                detallesResponse
        );


        return response;
    }


    /*
     * CONVERTIR DETALLE
     * A RESPONSE
     */

    private DetallePedidoResponse convertirDetalleAResponse(
            DetallePedido detalle
    ) {

        DetallePedidoResponse response =
                new DetallePedidoResponse();


        response.setId(
                detalle.getId()
        );


        response.setProductoId(
                detalle.getProducto().getId()
        );


        response.setProductoNombre(
                detalle.getProducto().getNombre()
        );


        response.setCantidad(
                detalle.getCantidad()
        );


        response.setPrecioUnitario(
                detalle.getPrecioUnitario()
        );


        response.setSubtotal(
                detalle.getSubtotal()
        );


        response.setOfertaAplicada(
                detalle.getOfertaRescate() != null
        );


        return response;
    }


    /*
     * LIMPIAR TEXTOS OPCIONALES
     */

    private String limpiarTexto(
            String texto
    ) {

        if (texto == null) {
            return null;
        }

        String resultado =
                texto.trim();

        return resultado.isEmpty()
                ? null
                : resultado;
    }
}