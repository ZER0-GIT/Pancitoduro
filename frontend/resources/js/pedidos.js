window.addEventListener("DOMContentLoaded", async () => {
    const listaPedidos = document.getElementById("listaPedidos");
    const mensajePedidos = document.getElementById("mensajePedidos");
    const tablaPedidos = document.getElementById("tablaPedidos");
    let detalleAbierto = null;

    /*
     * ==========================
     * VERIFICAR SESIÓN
     * ==========================
     */

    async function verificarSesion() {
        try {
            const response = await fetch(`${API_URL}/api/auth/me`, {
                method: "GET",
                credentials: "include"
            });

            if (!response.ok) {
                alert("Debes iniciar sesión para consultar tus pedidos.");
                window.location.href = "login.html";
                return false;
            }

            return true;

        } catch (error) {
            console.error("Error verificando sesión:", error);

            mensajePedidos.textContent =
                "No se pudo conectar con el servidor.";

            return false;
        }
    }

    /*
     * ==========================
     * CARGAR PEDIDOS
     * ==========================
     */

    async function cargarPedidos() {
        try {
            const response = await fetch(
                `${API_URL}/api/pedidos/mis-pedidos`,
                {
                    method: "GET",
                    credentials: "include"
                }
            );

            if (!response.ok) {
                throw new Error(
                    "No se pudieron consultar los pedidos."
                );
            }

            const pedidos = await response.json();
            mostrarPedidos(pedidos);

        } catch (error) {
            console.error("Error cargando pedidos:", error);

            tablaPedidos.style.display = "none";

            mensajePedidos.textContent =
                "No se pudieron cargar los pedidos.";
        }
    }

    /*
     * ==========================
     * MOSTRAR PEDIDOS
     * ==========================
     */

    function mostrarPedidos(pedidos) {

        listaPedidos.innerHTML = "";

        if (!Array.isArray(pedidos) || pedidos.length === 0) {

            tablaPedidos.style.display = "none";

            mensajePedidos.textContent =
                "Todavía no has realizado ningún pedido.";

            return;

        }

        tablaPedidos.style.display = "table";

        mensajePedidos.textContent = "";

        pedidos.forEach(pedido => {

            const filaPedido = document.createElement("tr");

            filaPedido.className = "filaPedido";

            const estado = formatearEstado(
                pedido.estado
            );

            const claseEstado =
                obtenerClaseEstado(
                    pedido.estado
                );

            filaPedido.innerHTML = `

                <td class="expandir">

                    ▶

                </td>

                <td class="colPedido">

                    #${pedido.id}

                </td>

                <td class="colFecha">

                    ${formatearFecha(
                        pedido.fechaPedido
                    )}

                </td>

                <td class="colTotal">

                    S/ ${Number(
                        pedido.total
                    ).toFixed(2)}

                </td>

                <td class="colEstado">

                    <span class="${claseEstado}">

                        ${estado}

                    </span>

                </td>

            `;

            const filaDetalle =
                document.createElement("tr");

            filaDetalle.className =
                "filaDetalle";

            filaDetalle.style.display =
                "none";

            filaDetalle.innerHTML = `

            <td colspan="5">

            <div class="detallePedido">

                <h3>

                    Información del Pedido

                </h3>

                <div class="detalleGrid">

                    <div>

                        <strong>Fecha</strong>

                        <span>

                            ${formatearFechaHora(
                                pedido.fechaPedido
                            )}

                        </span>

                    </div>

                    <div>

                        <strong>Dirección</strong>

                        <span>

                            ${pedido.direccionEntrega}

                        </span>

                    </div>

                    <div>

                        <strong>Teléfono</strong>

                        <span>

                            ${pedido.telefonoContacto}

                        </span>

                    </div>

                    <div>

                        <strong>Método de Pago</strong>

                        <span>

                            ${formatearMetodoPago(
                                pedido.metodoPago
                            )}

                        </span>

                    </div>

                    <div>

                        <strong>Estado del Pago</strong>

                        <span>

                            ${formatearEstadoPago(
                                pedido.estadoPago
                            )}

                        </span>

                    </div>

                    <div>

                        <strong>Observaciones</strong>

                        <span>

                            ${pedido.observaciones || "Sin observaciones"}

                        </span>

                    </div>

                </div>

                <h4>

                    Productos

                </h4>

                <table class="tablaDetalleProductos">

                    <thead>

                        <tr>

                            <th>Producto</th>

                            <th>Cant.</th>

                            <th>Precio</th>

                            <th>Subtotal</th>

                        </tr>

                    </thead>

                    <tbody>

                        ${pedido.detalles.map(detalle=>`

                        <tr>

                            <td>

                                ${detalle.productoNombre}

                            </td>

                            <td>

                                ${detalle.cantidad}

                            </td>

                            <td>

                                S/ ${Number(
                                    detalle.precioUnitario
                                ).toFixed(2)}

                            </td>

                            <td>

                                S/ ${Number(
                                    detalle.subtotal
                                ).toFixed(2)}

                            </td>

                        </tr>

                        `).join("")}

                    </tbody>

                </table>

            </div>

            </td>

            `;

            listaPedidos.appendChild(
                filaPedido
            );

            listaPedidos.appendChild(
                filaDetalle
            );

            filaPedido.addEventListener("click", () => {

                // Si ya hay otro abierto, cerrarlo

                if (detalleAbierto && detalleAbierto !== filaDetalle) {

                    detalleAbierto.style.display = "none";

                    detalleAbierto.previousElementSibling
                        .querySelector(".expandir")
                        .textContent = "▶";

                }

                // Abrir / cerrar el actual

                if (filaDetalle.style.display === "table-row") {

                    filaDetalle.style.display = "none";

                    filaPedido
                        .querySelector(".expandir")
                        .textContent = "▶";

                    detalleAbierto = null;

                } else {

                    filaDetalle.style.display = "table-row";

                    filaPedido
                        .querySelector(".expandir")
                        .textContent = "▼";

                    detalleAbierto = filaDetalle;

                }

            });

        });

    }

    /*
     * ==========================
     * MOSTRAR PRODUCTOS
     * ==========================
     */

    function crearTextoProductos(detalles) {
        if (!Array.isArray(detalles) || detalles.length === 0) {
            return "Sin productos";
        }

        return detalles
            .map(detalle => {
                return `
                    ${detalle.cantidad}x
                    ${detalle.productoNombre}
                `;
            })
            .join("<br>");
    }
    
    function formatearFecha(fecha) {

    const fechaPedido = new Date(fecha);

    return fechaPedido.toLocaleDateString(
        "es-PE",
        {
            day: "2-digit",
            month: "2-digit",
            year: "numeric"
        }
    );

}

    /*
     * ==========================
     * FORMATEAR ESTADO
     * ==========================
     */

    function formatearEstado(estado) {
        const estados = {
            PENDIENTE: "Pendiente",
            PREPARANDO: "En preparación",
            LISTO: "Listo",
            ENTREGADO: "Entregado",
            CANCELADO: "Cancelado"
        };

        return estados[estado] || estado;
    }
    function formatearMetodoPago(metodo) {

        const metodos = {

            TARJETA: "Tarjeta",

            YAPE_PLIN: "Yape / Plin",

            EFECTIVO: "Efectivo"

        };

        return metodos[metodo] || metodo;

    }

    function formatearEstadoPago(estado) {

        const estados = {

            PAGADO: "Pagado",

            PENDIENTE: "Pendiente",

            RECHAZADO: "Rechazado"

        };

        return estados[estado] || estado;

    }
    function formatearFechaHora(fecha){

        return new Date(fecha).toLocaleString(

            "es-PE",

            {

                day:"2-digit",

                month:"2-digit",

                year:"numeric",

                hour:"2-digit",

                minute:"2-digit"

            }

        );

    }

    /*
     * ==========================
     * CLASE CSS DEL ESTADO
     * ==========================
     */

    function obtenerClaseEstado(estado) {
        switch (estado) {
            case "ENTREGADO":
                return "estadoEntregado";

            case "CANCELADO":
                return "estadoCancelado";

            default:
                return "estadoProceso";
        }
    }

    /*
     * ==========================
     * INICIALIZAR PÁGINA
     * ==========================
     */

    const sesionValida = await verificarSesion();

    if (!sesionValida) {
        return;
    }

    await cargarPedidos();
});