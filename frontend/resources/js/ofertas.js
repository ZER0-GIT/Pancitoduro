window.addEventListener("DOMContentLoaded", async () => {
    const listaOfertas = document.getElementById("listaOfertas");
    const mensajeOfertas = document.getElementById("mensajeOfertas");
    const tablaOfertas = document.getElementById("tablaOfertas");

    /*
     * ==========================
     * OBTENER CARRITO
     * ==========================
     */

    function obtenerCarrito() {
        try {
            const carrito = JSON.parse(
                localStorage.getItem("carrito_pancitoduro")
            );

            return Array.isArray(carrito)
                ? carrito
                : [];

        } catch {
            return [];
        }
    }

    /*
     * ==========================
     * GUARDAR CARRITO
     * ==========================
     */

    function guardarCarrito(carrito) {
        localStorage.setItem(
            "carrito_pancitoduro",
            JSON.stringify(carrito)
        );
    }

    /*
     * ==========================
     * AGREGAR PRODUCTO
     * AL CARRITO
     * ==========================
     */

    function agregarAlCarrito(productoId, stock) {
        const carrito = obtenerCarrito();

        const productoExistente = carrito.find(
            item => item.productoId === productoId
        );

        /*
         * Evitamos agregar una cantidad
         * superior al stock disponible.
         */

        if (
            productoExistente &&
            productoExistente.cantidad >= stock
        ) {
            alert("No puedes añadir más unidades. Stock máximo alcanzado.");
            return;
        }

        if (productoExistente) {
            productoExistente.cantidad++;
        } else {
            carrito.push({
                productoId,
                cantidad: 1
            });
        }

        guardarCarrito(carrito);

        alert("Producto añadido al carrito.");
    }

    /*
     * ==========================
     * MOSTRAR OFERTAS
     * ==========================
     */

    function mostrarOfertas(ofertas) {
        listaOfertas.innerHTML = "";

        /*
         * Aunque el endpoint debería devolver
         * ofertas disponibles, filtramos nuevamente
         * por seguridad en el frontend.
         */

        const ofertasDisponibles = ofertas.filter(
            oferta =>
                oferta.activa === true &&
                oferta.stock > 0
        );

        if (ofertasDisponibles.length === 0) {
            tablaOfertas.style.display = "none";

            mensajeOfertas.textContent =
                "No hay ofertas disponibles actualmente.";

            return;
        }

        tablaOfertas.style.display = "table";
        mensajeOfertas.textContent = "";

        ofertasDisponibles.forEach(oferta => {
            const fila = document.createElement("tr");

            fila.innerHTML = `
                <td>
                    ${oferta.nombre}
                </td>

                <td>
                    ${oferta.descripcion ?? ""}
                </td>

                <td>
                    S/ ${Number(oferta.precioOriginal).toFixed(2)}
                </td>

                <td>
                    S/ ${Number(oferta.precioOferta).toFixed(2)}
                </td>

                <td>
                    ${oferta.stock}
                </td>

                <td class="accionCarrito">
                    <button
                        type="button"
                        class="btnAgregarProducto"
                        data-producto-id="${oferta.productoId}"
                        data-stock="${oferta.stock}"
                    >
                        <img
                            src="resources/icon/addCarrito.svg"
                            alt="Añadir al carrito"
                        >
                    </button>
                </td>
            `;

            listaOfertas.appendChild(fila);
        });
    }

    /*
     * ==========================
     * EVENTOS DE LA TABLA
     * ==========================
     */

    listaOfertas.addEventListener("click", event => {
        const boton = event.target.closest(
            ".btnAgregarProducto"
        );

        if (!boton) {
            return;
        }

        const productoId = Number(
            boton.dataset.productoId
        );

        const stock = Number(
            boton.dataset.stock
        );

        agregarAlCarrito(
            productoId,
            stock
        );
    });

    /*
     * ==========================
     * CARGAR OFERTAS
     * ==========================
     */

    async function cargarOfertas() {
        const response = await fetch(
            `${API_URL}/api/ofertas`
        );

        if (!response.ok) {
            throw new Error(
                "No se pudieron cargar las ofertas."
            );
        }

        return await response.json();
    }

    /*
     * ==========================
     * INICIALIZAR PÁGINA
     * ==========================
     */

    try {
        const ofertas = await cargarOfertas();

        mostrarOfertas(ofertas);

    } catch (error) {
        console.error(
            "Error cargando ofertas:",
            error
        );

        tablaOfertas.style.display = "none";

        mensajeOfertas.textContent =
            "No se pudieron cargar las ofertas.";
    }
});