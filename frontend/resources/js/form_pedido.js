let productos = [];
let ofertas = [];
let usuarioActual = null;

window.addEventListener("DOMContentLoaded", async () => {
    const formulario = document.getElementById("form-pedido");
    const listaProductos = document.getElementById("lista-productos");
    const inputNombre = document.getElementById("nombre");
    const inputTelefono = document.getElementById("telefono");
    const inputDireccion = document.getElementById("direccion");
    const inputObservaciones = document.getElementById("observaciones");
    const textoTotal = document.getElementById("Total");

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
     * VERIFICAR SESIÓN
     * ==========================
     */

    async function verificarSesion() {
        const response = await fetch(
            `${API_URL}/api/auth/me`,
            {
                method: "GET",
                credentials: "include"
            }
        );

        if (!response.ok) {
            alert("Debes iniciar sesión para realizar un pedido.");
            window.location.href = "login.html";
            return false;
        }

        const data = await response.json();

        /*
        * El backend devuelve:
        *
        * {
        *     usuario: {
        *         id,
        *         nombre,
        *         apellido,
        *         correo,
        *         telefono,
        *         rol
        *     }
        * }
        */

        usuarioActual = data.usuario;

        inputNombre.value =
            `${usuarioActual.nombre ?? ""} ${usuarioActual.apellido ?? ""}`.trim();

        inputTelefono.value =
            usuarioActual.telefono ?? "";

        return true;
    }

    /*
     * ==========================
     * CARGAR PRODUCTOS
     * ==========================
     */

    async function cargarProductos() {
        const response = await fetch(
            `${API_URL}/api/productos`
        );

        if (!response.ok) {
            throw new Error(
                "No se pudieron cargar los productos."
            );
        }

        productos = await response.json();
    }

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

        ofertas = await response.json();
    }

    /*
     * ==========================
     * BUSCAR OFERTA ACTIVA
     * ==========================
     */

    function buscarOferta(productoId) {
        return ofertas.find(
            oferta =>
                oferta.productoId === productoId &&
                oferta.activa === true
        );
    }

    /*
     * ==========================
     * OBTENER PRECIO ACTUAL
     * ==========================
     */

    function obtenerPrecioProducto(producto) {
        const oferta = buscarOferta(producto.id);

        if (oferta) {
            return Number(oferta.precioOferta);
        }

        return Number(producto.precio);
    }

    /*
     * ==========================
     * MOSTRAR CARRITO
     * ==========================
     */

    function mostrarCarrito() {
        listaProductos.innerHTML = "";

        let carrito = obtenerCarrito();

        /*
         * Eliminamos productos que ya no existen,
         * están desactivados o no tienen stock.
         */

        carrito = carrito.filter(item => {
            const producto = productos.find(
                producto =>
                    producto.id === item.productoId
            );

            return (
                producto &&
                producto.activo === true &&
                producto.stock > 0
            );
        });

        /*
         * Ajustamos cantidades al stock disponible.
         */

        carrito.forEach(item => {
            const producto = productos.find(
                producto =>
                    producto.id === item.productoId
            );

            item.cantidad = Math.min(
                Math.max(Number(item.cantidad), 1),
                producto.stock
            );
        });

        guardarCarrito(carrito);

        if (carrito.length === 0) {
            listaProductos.innerHTML = `
                <p>No hay productos en el carrito.</p>
            `;

            textoTotal.textContent =
                "Total estimado: S/ 0.00";

            return;
        }

        carrito.forEach(item => {
            const producto = productos.find(
                producto =>
                    producto.id === item.productoId
            );

            const oferta =
                buscarOferta(producto.id);

            const precioActual =
                obtenerPrecioProducto(producto);

            const contenedor =
                document.createElement("div");

            contenedor.className =
                "checkbox-card";

            contenedor.innerHTML = `
                <div>
                    <span class="plato-name">
                        ${producto.nombre}
                    </span>

                    <span class="plato-desc">
                        ${producto.descripcion ?? ""}
                    </span>
                </div>

                <span class="plato-price">
                    ${
                        oferta
                            ? `
                                <del>
                                    S/ ${Number(producto.precio).toFixed(2)}
                                </del>

                                S/ ${precioActual.toFixed(2)}
                              `
                            : `
                                S/ ${precioActual.toFixed(2)}
                              `
                    }
                </span>

                <input
                    type="number"
                    class="producto-cantidad"
                    data-producto-id="${producto.id}"
                    min="1"
                    max="${producto.stock}"
                    value="${item.cantidad}"
                >

                <button
                    type="button"
                    class="btnEliminarProducto"
                    data-producto-id="${producto.id}"
                >
                    Eliminar
                </button>
            `;

            listaProductos.appendChild(
                contenedor
            );
        });

        actualizarTotal();
    }

    /*
     * ==========================
     * CAMBIAR CANTIDAD
     * ==========================
     */

    listaProductos.addEventListener("change", event => {
        if (
            !event.target.classList.contains(
                "producto-cantidad"
            )
        ) {
            return;
        }

        const productoId =
            Number(event.target.dataset.productoId);

        const producto = productos.find(
            producto =>
                producto.id === productoId
        );

        if (!producto) {
            return;
        }

        let cantidad =
            Number(event.target.value);

        if (!Number.isInteger(cantidad) || cantidad < 1) {
            cantidad = 1;
        }

        if (cantidad > producto.stock) {
            cantidad = producto.stock;
        }

        event.target.value = cantidad;

        const carrito = obtenerCarrito();

        const item = carrito.find(
            item =>
                item.productoId === productoId
        );

        if (item) {
            item.cantidad = cantidad;

            guardarCarrito(carrito);
        }

        actualizarTotal();
    });

    /*
     * ==========================
     * ELIMINAR PRODUCTO
     * ==========================
     */

    function eliminarProducto(productoId) {
        const carrito = obtenerCarrito().filter(
            item =>
                item.productoId !== productoId
        );

        guardarCarrito(carrito);
    }

    listaProductos.addEventListener("click", event => {
        const boton = event.target.closest(
            ".btnEliminarProducto"
        );

        if (!boton) {
            return;
        }

        const productoId =
            Number(boton.dataset.productoId);

        eliminarProducto(productoId);

        mostrarCarrito();
    });

    /*
     * ==========================
     * CALCULAR TOTAL ESTIMADO
     * ==========================
     */

    function actualizarTotal() {
        const carrito = obtenerCarrito();

        let total = 0;

        carrito.forEach(item => {
            const producto = productos.find(
                producto =>
                    producto.id === item.productoId
            );

            if (!producto) {
                return;
            }

            const precioActual =
                obtenerPrecioProducto(producto);

            total +=
                precioActual *
                Number(item.cantidad);
        });

        textoTotal.textContent =
            `Total estimado: S/ ${total.toFixed(2)}`;

        return total;
    }

    /*
     * ==========================
     * OBTENER DETALLES PEDIDO
     * ==========================
     */

    function obtenerDetallesPedido() {
        return obtenerCarrito().map(item => ({
            productoId: item.productoId,
            cantidad: item.cantidad
        }));
    }

    /*
     * ==========================
     * MOSTRAR PAGO
     * ==========================
     */

    function mostrarPago() {
        if (
            document.getElementById(
                "contenedor-pago"
            )
        ) {
            return;
        }

        const totalEstimado =
            actualizarTotal();

        const contenedor =
            document.createElement("div");

        contenedor.id =
            "contenedor-pago";

        contenedor.innerHTML = `
            <h3>Proceso de Pago</h3>

            <p>
                Importe estimado:
                S/ ${totalEstimado.toFixed(2)}
            </p>

            <p>
                Selecciona un método de pago
            </p>

            <button
                type="button"
                id="btnTarjeta"
            >
                Tarjeta
            </button>

            <button
                type="button"
                id="btnYape"
            >
                Yape / Plin
            </button>

            <button
                type="button"
                id="btnConfirmarCompra"
                style="display:none;"
            >
                Confirmar Compra
            </button>
        `;

        formulario.appendChild(contenedor);

        configurarPago();
    }

    /*
     * ==========================
     * CONFIGURAR PAGO
     * ==========================
     */

    function configurarPago() {
        const btnTarjeta =
            document.getElementById("btnTarjeta");

        const btnYape =
            document.getElementById("btnYape");

        const btnConfirmar =
            document.getElementById(
                "btnConfirmarCompra"
            );

        let metodoPago = null;

        btnTarjeta.addEventListener("click", () => {
            metodoPago = "TARJETA";

            btnConfirmar.style.display =
                "inline-block";
        });

        btnYape.addEventListener("click", () => {
            metodoPago = "YAPE_PLIN";

            btnConfirmar.style.display =
                "inline-block";
        });

        btnConfirmar.addEventListener(
            "click",
            async () => {
                if (!metodoPago) {
                    alert(
                        "Selecciona un método de pago."
                    );

                    return;
                }

                await crearPedido(metodoPago);
            }
        );
    }

    /*
     * ==========================
     * CREAR PEDIDO
     * ==========================
     */

    async function crearPedido(metodoPago) {
        const detalles =
            obtenerDetallesPedido();

        if (detalles.length === 0) {
            alert("Tu carrito está vacío.");
            return;
        }

        const request = {
            direccionEntrega:
                inputDireccion.value.trim(),

            telefonoContacto:
                inputTelefono.value.trim(),

            observaciones:
                inputObservaciones
                    ? inputObservaciones.value.trim()
                    : "",

            metodoPago,

            detalles
        };

        try {
            const response = await fetch(
                `${API_URL}/api/pedidos`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    credentials: "include",

                    body:
                        JSON.stringify(request)
                }
            );

            let data = null;

            try {
                data = await response.json();
            } catch {
                data = null;
            }

            if (!response.ok) {
                alert(
                    data?.mensaje ||
                    "No se pudo realizar el pedido."
                );

                return;
            }

            alert(
                `Pedido realizado correctamente.

Pedido #${data.id}

Total pagado: S/ ${Number(data.total).toFixed(2)}`
            );

            /*
             * Eliminamos el carrito solamente
             * cuando el backend confirma
             * correctamente el pedido.
             */

            localStorage.removeItem(
                "carrito_pancitoduro"
            );

            window.location.href =
                "pedidos.html";

        } catch (error) {
            console.error(
                "Error creando pedido:",
                error
            );

            alert(
                "No se pudo conectar con el servidor."
            );
        }
    }

    /*
     * ==========================
     * ENVIAR FORMULARIO
     * ==========================
     */

    formulario.addEventListener("submit", event => {
        event.preventDefault();

        const carrito =
            obtenerCarrito();

        if (carrito.length === 0) {
            alert("Tu carrito está vacío.");
            return;
        }

        if (inputDireccion.value.trim() === "") {
            alert(
                "Ingresa una dirección de entrega."
            );

            return;
        }

        if (inputTelefono.value.trim() === "") {
            alert(
                "Ingresa un teléfono de contacto."
            );

            return;
        }

        mostrarPago();
    });

    /*
     * ==========================
     * INICIALIZAR
     * ==========================
     */

    try {
        const sesionValida =
            await verificarSesion();

        if (!sesionValida) {
            return;
        }

        /*
         * Productos y ofertas deben cargarse
         * antes de mostrar el carrito.
         */

        await Promise.all([
            cargarProductos(),
            cargarOfertas()
        ]);

        mostrarCarrito();

    } catch (error) {
        console.error(
            "Error inicializando formulario:",
            error
        );

        listaProductos.innerHTML = `
            <p>No se pudo cargar el carrito.</p>
        `;
    }
});