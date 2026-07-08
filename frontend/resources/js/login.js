const API_URL = "https://api.pancitoduro.cafe";

window.addEventListener("DOMContentLoaded", () => {
    const formularioLogin = document.getElementById("loginForm");
    const correoInput = document.getElementById("correo");
    const passwordInput = document.getElementById("password");
    const botonLogin = document.getElementById("btnLogin");
    const mensajeLogin = document.getElementById("mensajeLogin");

    formularioLogin.addEventListener("submit", async (event) => {
        event.preventDefault();

        const correo = correoInput.value.trim();
        const password = passwordInput.value;

        limpiarMensaje();

        if (correo === "" || password === "") {
            mostrarMensaje("Por favor, completa todos los campos.", "error");
            return;
        }

        bloquearFormulario(true);

        try {
            const response = await fetch(`${API_URL}/api/auth/login`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                credentials: "include",
                body: JSON.stringify({
                    correo,
                    password
                })
            });

            const data = await obtenerRespuestaJSON(response);

            if (!response.ok) {
                mostrarMensaje(
                    data?.mensaje || "Correo o contraseña incorrectos.",
                    "error"
                );

                bloquearFormulario(false);
                return;
            }

            mostrarMensaje(
                `Bienvenido, ${data.usuario.nombre}.`,
                "exito"
            );

            window.location.href = "index.html";

        } catch (error) {
            console.error("Error al iniciar sesión:", error);

            mostrarMensaje(
                "No se pudo conectar con el servidor.",
                "error"
            );

            bloquearFormulario(false);
        }
    });

    async function obtenerRespuestaJSON(response) {
        try {
            return await response.json();
        } catch {
            return null;
        }
    }

    function mostrarMensaje(mensaje, tipo) {
        mensajeLogin.textContent = mensaje;
        mensajeLogin.className = tipo;
    }

    function limpiarMensaje() {
        mensajeLogin.textContent = "";
        mensajeLogin.className = "";
    }

    function bloquearFormulario(bloquear) {
        botonLogin.disabled = bloquear;
        botonLogin.textContent = bloquear
            ? "Ingresando..."
            : "Ingresar";
    }
});