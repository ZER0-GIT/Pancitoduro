window.addEventListener("DOMContentLoaded", async () => {
    const contenedorCategorias =
        document.getElementById("contenedorCategorias");

    const mensajeMenu =
        document.getElementById("mensajeMenu");

    let categorias = [];
    let productos = [];

    /*
     * ==========================
     * CARGAR CATEGORÍAS
     * ==========================
     */

    async function cargarCategorias() {
        const response = await fetch(
            `${API_URL}/api/categorias`
        );

        if (!response.ok) {
            throw new Error(
                "No se pudieron cargar las categorías."
            );
        }

        categorias = await response.json();
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
     * OBTENER PRODUCTOS ACTIVOS
     * DE UNA CATEGORÍA
     * ==========================
     */

    function obtenerProductosCategoria(categoriaId) {
        return productos.filter(producto =>
            producto.categoriaId === categoriaId &&
            producto.activo === true &&
            producto.stock > 0
        );
    }

    /*
     * ==========================
     * MOSTRAR CATEGORÍAS
     * ==========================
     */

    function mostrarCategorias() {
        contenedorCategorias.innerHTML = "";

        if (categorias.length === 0) {
            mensajeMenu.textContent =
                "No hay categorías disponibles.";

            return;
        }

        mensajeMenu.textContent = "";

        categorias.forEach(categoria => {
            const productosCategoria =
                obtenerProductosCategoria(categoria.id);

            const seccion =
                document.createElement("section");

            seccion.className =
                "menuProductos";

            seccion.innerHTML = `
                <h3>${categoria.nombre}</h3>

                <table class="tablaMenu">
                    <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Descripción</th>
                            <th>Precio</th>
                            <th>Acción</th>
                        </tr>
                    </thead>

                    <tbody id="categoria-${categoria.id}">
                    </tbody>
                </table>
            `;

            contenedorCategorias.appendChild(
                seccion
            );

            const cuerpoTabla =
                document.getElementById(
                    `categoria-${categoria.id}`
                );

            mostrarProductos(
                cuerpoTabla,
                productosCategoria
            );
        });
    }

    /*
     * ==========================
     * MOSTRAR PRODUCTOS
     * ==========================
     */

    function mostrarProductos(
        cuerpoTabla,
        productosCategoria
    ) {
        if (productosCategoria.length === 0) {
            cuerpoTabla.innerHTML = `
                <tr>
                    <td colspan="4">
                        No hay productos disponibles
                        en esta categoría.
                    </td>
                </tr>
            `;

            return;
        }

        productosCategoria.forEach(producto => {
            const fila =
                document.createElement("tr");

            fila.innerHTML = `
                <td>
                    ${producto.nombre}
                </td>

                <td>
                    ${producto.descripcion ?? ""}
                </td>

                <td>
                    S/ ${Number(producto.precio).toFixed(2)}
                </td>

                <td class="accionCarrito">
                    <button
                        type="button"
                        class="btnAgregarProducto"
                        data-producto-id="${producto.id}"
                    >
                        <img
                            src="resources/icon/addCarrito.svg"
                            alt="Añadir al carrito"
                        >
                    </button>
                </td>
            `;

            cuerpoTabla.appendChild(fila);
        });
    }

    /*
     * ==========================
     * AGREGAR PRODUCTO
     * AL CARRITO
     * ==========================
     */

    function agregarAlCarrito(productoId) {
        const carrito = JSON.parse(
            localStorage.getItem(
                "carrito_pancitoduro"
            )
        ) || [];

        const productoExistente =
            carrito.find(
                item =>
                    item.productoId === productoId
            );

        /*
         * Si el producto ya existe,
         * incrementamos su cantidad.
         */

        if (productoExistente) {
            productoExistente.cantidad++;
        } else {
            carrito.push({
                productoId,
                cantidad: 1
            });
        }

        localStorage.setItem(
            "carrito_pancitoduro",
            JSON.stringify(carrito)
        );

        alert(
            "Producto añadido al carrito."
        );
    }

    /*
     * ==========================
     * EVENTOS DEL CARRITO
     * ==========================
     */

    contenedorCategorias.addEventListener(
        "click",
        event => {
            const boton =
                event.target.closest(
                    ".btnAgregarProducto"
                );

            if (!boton) {
                return;
            }

            const productoId =
                Number(
                    boton.dataset.productoId
                );

            agregarAlCarrito(
                productoId
            );
        }
    );

    /*
     * ==========================
     * INICIALIZAR MENÚ
     * ==========================
     */

    try {
        await Promise.all([
            cargarCategorias(),
            cargarProductos()
        ]);

        mostrarCategorias();

    } catch (error) {
        console.error(
            "Error cargando el menú:",
            error
        );

        mensajeMenu.textContent =
            "No se pudo cargar el menú.";

        contenedorCategorias.innerHTML = "";
    }
});