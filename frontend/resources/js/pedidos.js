window.addEventListener("DOMContentLoaded", async () => {
    const listaPedidos = document.getElementById("listaPedidos");
    const mensajePedidos = document.getElementById("mensajePedidos");
    const tablaPedidos = document.getElementById("tablaPedidos");

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
            const fila = document.createElement("tr");

            const productos = crearTextoProductos(
                pedido.detalles
            );

            const estado = formatearEstado(
                pedido.estado
            );

            const claseEstado = obtenerClaseEstado(
                pedido.estado
            );

            fila.innerHTML = `
                <td>
                    #${pedido.id}
                </td>

                <td>
                    ${pedido.usuarioNombre}
                </td>

                <td>
                    ${productos}
                </td>

                <td>
                    S/ ${Number(pedido.total).toFixed(2)}
                </td>

                <td>
                    <span class="${claseEstado}">
                        ${estado}
                    </span>
                </td>
            `;

            listaPedidos.appendChild(fila);
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