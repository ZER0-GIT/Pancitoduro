window.addEventListener("DOMContentLoaded", () => {
    const formularioLogin = document.getElementById("loginForm");
    const correoInput = document.getElementById("correo");
    const passwordInput = document.getElementById("password");
    const botonLogin = document.getElementById("btnLogin");
    const mensajeLogin = document.getElementById("mensajeLogin");

    if (!formularioLogin) {
        return;
    }

    formularioLogin.addEventListener("submit", async (event) => {
        event.preventDefault();

        const correo = correoInput.value.trim();
        const password = passwordInput.value;

        limpiarMensaje();

        // Validación básica del formulario
        if (correo === "" || password === "") {
            mostrarMensaje(
                "Por favor, completa todos los campos.",
                "error"
            );
            return;
        }

        bloquearFormulario(true);

        try {
            // Enviar las credenciales al backend
            const response = await fetch(
                `${API_URL}/api/auth/login`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    credentials: "include",
                    body: JSON.stringify({
                        correo,
                        password
                    })
                }
            );

            const data = await obtenerRespuestaJSON(response);

            if (!response.ok) {
                mostrarMensaje(
                    data?.mensaje ||
                    "Correo o contraseña incorrectos.",
                    "error"
                );

                bloquearFormulario(false);
                return;
            }

            // Verificar que la sesión fue creada correctamente
            const responseSesion = await fetch(
                `${API_URL}/api/auth/me`,
                {
                    method: "GET",
                    credentials: "include"
                }
            );

            if (!responseSesion.ok) {
                mostrarMensaje(
                    "No se pudo conservar la sesión.",
                    "error"
                );

                bloquearFormulario(false);
                return;
            }

            mostrarMensaje(
                `Bienvenido, ${data.usuario.nombre}.`,
                "exito"
            );

            // Redirigir al inicio después del login exitoso
            setTimeout(() => {
                window.location.href = "index.html";
            }, 500);

        } catch (error) {
            mostrarMensaje(
                "No se pudo conectar con el servidor.",
                "error"
            );

            bloquearFormulario(false);
        }
    });

    // Intenta convertir la respuesta del backend a JSON
    async function obtenerRespuestaJSON(response) {
        try {
            return await response.json();
        } catch {
            return null;
        }
    }

    // Muestra mensajes de error o éxito en el formulario
    function mostrarMensaje(mensaje, tipo) {
        mensajeLogin.textContent = mensaje;
        mensajeLogin.className = tipo;
    }

    // Limpia mensajes anteriores
    function limpiarMensaje() {
        mensajeLogin.textContent = "";
        mensajeLogin.className = "";
    }

    // Evita múltiples peticiones mientras se procesa el login
    function bloquearFormulario(bloquear) {
        botonLogin.disabled = bloquear;

        botonLogin.textContent = bloquear
            ? "Ingresando..."
            : "Ingresar";
    }
});