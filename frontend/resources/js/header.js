window.addEventListener("DOMContentLoaded", async () => {
    const headerUsuario =
        document.getElementById("headerUsuario");

    if (!headerUsuario) {
        return;
    }

    /*
     * ==========================
     * CONSULTAR SESIÓN
     * ==========================
     */

    async function obtenerUsuarioActual() {
        try {
            const response = await fetch(
                `${API_URL}/api/auth/me`,
                {
                    method: "GET",
                    credentials: "include"
                }
            );

            if (!response.ok) {
                return null;
            }

            const data = await response.json();
            return data.usuario;

        } catch (error) {
            console.error(
                "Error consultando la sesión:",
                error
            );

            return null;
        }
    }

    /*
     * ==========================
     * MOSTRAR USUARIO
     * ==========================
     */

    function mostrarUsuario(usuario) {
        headerUsuario.innerHTML = `
            <span class="headerNombreUsuario">
                ${usuario.nombre}
                ${usuario.apellido}
            </span>

            <a
                href="form_pedido.html"
                class="headerCarrito"
                aria-label="Ver carrito"
            >
                🛒
            </a>

            <button
                type="button"
                id="btnCerrarSesion"
                class="btnCerrarSesion"
            >
                Cerrar sesión
            </button>
        `;

        actualizarCantidadCarrito();

        document
            .getElementById("btnCerrarSesion")
            .addEventListener(
                "click",
                cerrarSesion
            );
    }

    /*
     * ==========================
     * MOSTRAR LOGIN
     * ==========================
     */

    function mostrarBotonesLogin() {
        headerUsuario.innerHTML = `
            <div class="btnLogin">
                <a href="login.html">
                    Iniciar sesión
                </a>
            </div>

            <div class="btnRegister">
                <a href="registrar.html">
                    Registrarse
                </a>
            </div>
        `;
    }

    /*
     * ==========================
     * CERRAR SESIÓN
     * ==========================
     */

    async function cerrarSesion() {
        try {
            const response = await fetch(
                `${API_URL}/api/auth/logout`,
                {
                    method: "POST",
                    credentials: "include"
                }
            );

            if (!response.ok) {
                alert(
                    "No se pudo cerrar la sesión."
                );

                return;
            }
            localStorage.removeItem(
                "carrito_pancitoduro"
            );

            window.location.href =
                "index.html";

        } catch (error) {
            console.error(
                "Error cerrando sesión:",
                error
            );

            alert(
                "No se pudo conectar con el servidor."
            );
        }
    }

    /*
     * ==========================
     * INICIALIZAR HEADER
     * ==========================
     */

    const usuario =
        await obtenerUsuarioActual();

    if (usuario) {
        mostrarUsuario(usuario);
    } else {
        mostrarBotonesLogin();
    }
});